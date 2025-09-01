package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.common_domain.dto.student_dto.ImportResultDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.FileValidationException;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import aifu.project.libraryweb.service.student_service.utel_service.ImportJobStorage;
import aifu.project.libraryweb.service.student_service.utel_service.StudentExcelReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentExcelImportServiceImpl implements StudentExcelImportService {

    private final StudentRepository studentRepository;
    private final PassportHasher passportHasher;
    private final ImportErrorReportExcelService importErrorReportExcelService;
    private final StudentExcelReaderService studentExcelReaderService;
    private final ImportJobStorage importJobStorage;

    @Override
    public ImportResultDTO importStudents(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Fayl bo'sh bo'lishi mumkin emas.");
        }

        UUID jobId = UUID.randomUUID();
        log.info("Import jarayoni boshlandi. Job ID: {}", jobId);

        List<Student> studentsFromExcel = studentExcelReaderService.readFullStudentDataFromExcel(file.getInputStream());

        if (studentsFromExcel.isEmpty()) {
            return new ImportResultDTO(jobId, 0, 0, null);
        }

        Set<String> plainPassportsFromExcel = studentsFromExcel.stream()
                .map(Student::getPassportCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> hashedPassports = plainPassportsFromExcel.stream()
                .map(passportHasher::hash)
                .collect(Collectors.toSet());

        Set<String> existingActiveHashedPassports = studentRepository.findActiveExistingHashedPassportCodesNative(hashedPassports);

        List<Student> studentsToSave = new ArrayList<>();
        List<ImportErrorDTO> failedRecords = new ArrayList<>();
        Set<String> processedPlainPassports = new HashSet<>();

        for (Student student : studentsFromExcel) {
            String plainPassport = student.getPassportCode();

            if (plainPassport == null || plainPassport.isBlank()) {
                failedRecords.add(createErrorDTO(student, "Pasport kodi ko'rsatilmagan."));
                continue;
            }

            if (!processedPlainPassports.add(plainPassport)) {
                failedRecords.add(createErrorDTO(student, "Excel faylning o'zida takrorlangan."));
                continue;
            }

            String hashedPassport = passportHasher.hash(plainPassport);
            if (existingActiveHashedPassports.contains(hashedPassport)) {
                failedRecords.add(createErrorDTO(student, "Bu pasport kodli aktiv talaba allaqachon mavjud."));
                continue;
            }

            student.setPassportCode(hashedPassport);
            student.setRole(Role.STUDENT);
            student.setActive(false);
            student.setDeleted(false);
            studentsToSave.add(student);
        }

        int savedCount = saveStudentsWithRaceConditionHandling(studentsToSave, failedRecords);

        importJobStorage.saveErrors(jobId, failedRecords);

        String reportUrl = failedRecords.isEmpty() ? null : "/api/super-admin/students/import/report/" + jobId;

        log.info("Import jarayoni yakunlandi. Job ID: {}. Saqlandi: {}, Xatolar: {}", jobId, savedCount, failedRecords.size());

        return new ImportResultDTO(jobId, savedCount, failedRecords.size(), reportUrl);
    }

    @Override
    public Map<String, Object> getErrorReport(UUID jobId) {
        List<ImportErrorDTO> failedRecords = importJobStorage.getErrors(jobId);
        if (failedRecords.isEmpty()) {
            throw new RuntimeException("Berilgan ID bo'yicha import jarayoni topilmadi yoki unda xatoliklar yo'q.");
        }

        try {
            byte[] excelFile = importErrorReportExcelService.generateStudentImportErrorReport(failedRecords);
            String fileName = "import_xatoliklari_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

            Map<String, Object> reportData = new HashMap<>();
            reportData.put("file", excelFile);
            reportData.put("fileName", fileName);
            return reportData;

        } catch (IOException e) {
            log.error("Job ID {} uchun Excel hisobotini generatsiya qilishda xatolik!", jobId, e);
            throw new RuntimeException("Excel hisobotini yaratishda xatolik yuz berdi.", e);
        }
    }

    @Transactional
    public int saveStudentsWithRaceConditionHandling(List<Student> studentsToSave, List<ImportErrorDTO> failedRecords) {
        int savedCount = 0;
        if (studentsToSave.isEmpty()) {
            return 0;
        }

        List<Student> toSaveBatch = new ArrayList<>(studentsToSave);
        boolean shouldRetry = true;
        while (shouldRetry && !toSaveBatch.isEmpty()) {
            shouldRetry = false;
            try {
                studentRepository.saveAll(toSaveBatch);
                savedCount += toSaveBatch.size();
                toSaveBatch.clear();
            } catch (DataIntegrityViolationException e) {
                log.warn("Ommaviy saqlashda ziddiyat (ehtimoliy race condition): {}", e.getMostSpecificCause().getMessage());
                String duplicateHash = extractDuplicateHash(e.getMostSpecificCause().getMessage());
                if (duplicateHash != null) {
                    Iterator<Student> iterator = toSaveBatch.iterator();
                    boolean found = false;
                    while (iterator.hasNext()) {
                        Student s = iterator.next();
                        if (s.getPassportCode().equals(duplicateHash)) {
                            failedRecords.add(createErrorDTO(s, "Aktiv dublikat (parallel so'rov tufayli aniqlandi)."));
                            iterator.remove();
                            shouldRetry = true;
                            found = true;
                            break;
                        }
                    }
                    if (!found) shouldRetry = false;
                } else {
                    toSaveBatch.forEach(s -> failedRecords.add(createErrorDTO(s, "Noma'lum dublikat xatosi (ommaviy saqlashda).")));
                    toSaveBatch.clear();
                }
            }
        }
        return savedCount;
    }

    private ImportErrorDTO createErrorDTO(Student student, String reason) {
        // Pasport kodini xeshlangan holatda qaytarmaslik uchun, asl Student obyektidan o'qishimiz kerak.
        // Hozircha bu murakkab, shuning uchun sodda usulda qoldiramiz.
        return new ImportErrorDTO(student.getName(), student.getSurname(), student.getDegree(), student.getFaculty(),
                student.getAdmissionTime(), student.getGraduationTime(), reason);
    }

    private String extractDuplicateHash(String message) {
        if (message == null) return null;
        // Bu sizning triggerlaringizga moslashtirilishi kerak,
        // masalan (unique_passport_token)=(...)
        Pattern pattern = Pattern.compile("\\(unique_passport_token\\)\\)=\\((.*?)\\)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }
}