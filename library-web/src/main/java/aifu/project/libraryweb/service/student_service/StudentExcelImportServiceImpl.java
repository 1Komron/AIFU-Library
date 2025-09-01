package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.FileDownloadDTO;
import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.common_domain.dto.student_dto.ImportResultDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.FileValidationException;
import aifu.project.common_domain.exceptions.ReportGenerationException;
import aifu.project.common_domain.exceptions.ReportNotFoundException;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import aifu.project.libraryweb.service.student_service.utel_service.ImportJobStorage;
import aifu.project.libraryweb.service.student_service.utel_service.StudentExcelReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
    @Transactional
    public ImportResultDTO importStudents(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Fayl bo'sh bo'lishi mumkin emas.");
        }

        UUID jobId = UUID.randomUUID();
        log.info("Import jarayoni boshlandi. Job ID: {}", jobId);

        try (InputStream inputStream = file.getInputStream()) {
            List<Student> studentsFromExcel = studentExcelReaderService.readFullStudentDataFromExcel(inputStream);

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

                // Asl ma'lumotlar bilan xatolik DTO'sini oldindan tayyorlab olamiz
                ImportErrorDTO errorTemplate = createErrorDTO(student, "");

                if (plainPassport == null || plainPassport.isBlank()) {
                    errorTemplate.setErrorReason("Pasport kodi ko'rsatilmagan.");
                    failedRecords.add(errorTemplate);
                    continue;
                }

                if (!processedPlainPassports.add(plainPassport)) {
                    errorTemplate.setErrorReason("Excel faylning o'zida takrorlangan.");
                    failedRecords.add(errorTemplate);
                    continue;
                }

                String hashedPassport = passportHasher.hash(plainPassport);
                if (existingActiveHashedPassports.contains(hashedPassport)) {
                    errorTemplate.setErrorReason("Bu pasport kodli aktiv talaba allaqachon mavjud.");
                    failedRecords.add(errorTemplate);
                    continue;
                }

                // Barcha tekshiruvlardan o'tgandan keyingina passportni xeshlaymiz
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

        } catch (IOException e) {
            log.error("Fayl oqimini o'qishda xatolik yuz berdi: {}", e.getMessage());
            throw new FileValidationException("Faylni o'qishda xatolik yuz berdi. Iltimos, fayl tuzilishini tekshiring.");
        }
    }

    @Override
    public FileDownloadDTO getErrorReport(UUID jobId) {
        log.info("{} ID'li import jarayonining xatoliklar hisoboti so'raldi.", jobId);
        List<ImportErrorDTO> failedRecords = importJobStorage.getErrors(jobId);
        if (failedRecords.isEmpty()) {
            throw new ReportNotFoundException(jobId, "Import xatoliklari");
        }

        try {
            byte[] excelFile = importErrorReportExcelService.generateStudentImportErrorReport(failedRecords);
            String fileName = "import_xatoliklari_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
            log.info("Job ID {} uchun '{}' nomli hisobot muvaffaqiyatli yaratildi.", jobId, fileName);
            return new FileDownloadDTO(fileName, new ByteArrayResource(excelFile));
        } catch (IOException e) {
            throw new ReportGenerationException(jobId, "Import xatoliklari", e);
        }
    }

    @Override
    public FileDownloadDTO getTemplateFile() {
        log.info("Excel shablonini yuklab berish so'rovi qabul qilindi.");
        String templatePath = "templates/student_import_template.xlsx";
        String fileName = "student_import_template.xlsx";

        try {
            Resource resource = new ClassPathResource(templatePath);
            if (!resource.exists()) {
                log.error("Excel shabloni topilmadi: 'resources/{}'", templatePath);
                throw new RuntimeException("Import shabloni serverda topilmadi. Resurs yo'li: " + templatePath);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                byte[] fileData = inputStream.readAllBytes();
                log.info("'{}' shabloni muvaffaqiyatli o'qildi.", fileName);
                return new FileDownloadDTO(fileName, new ByteArrayResource(fileData));
            }

        } catch (IOException e) {
            log.error("Excel shablonini o'qishda kutilmagan xatolik", e);
            throw new RuntimeException("Shablon faylini o'qishda xatolik yuz berdi.", e);
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
                    while (iterator.hasNext()) {
                        Student s = iterator.next();
                        if (s.getPassportCode().equals(duplicateHash)) {
                            failedRecords.add(createErrorDTO(s, "Aktiv dublikat (parallel so'rov tufayli aniqlandi)."));
                            iterator.remove();
                            shouldRetry = true;
                            break;
                        }
                    }
                } else {
                    toSaveBatch.forEach(s -> failedRecords.add(createErrorDTO(s, "Noma'lum dublikat xatosi (ommaviy saqlashda).")));
                    toSaveBatch.clear();
                }
            }
        }
        return savedCount;
    }

    private ImportErrorDTO createErrorDTO(Student student, String reason) {
        // Bu metod endi har doim xeshlanmagan passport kodli Student obyektini oladi
        return new ImportErrorDTO(student.getName(), student.getSurname(), student.getDegree(), student.getFaculty(),
                student.getAdmissionTime(), student.getGraduationTime(), reason);
    }

    private String extractDuplicateHash(String message) {
        if (message == null) return null;

        // Unikal cheklov triggerdagi `unique_passport_token` ustuniga qo'yilgan
        Pattern pattern = Pattern.compile("\\(unique_passport_token\\)\\)=\\((.*?)\\)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }

        // Agar passport bo'yicha topilmasa, card token bo'yicha ham tekshiramiz
        pattern = Pattern.compile("\\(unique_card_token\\)\\)=\\((.*?)\\)");
        matcher = pattern.matcher(message);

        return matcher.find() ? matcher.group(1) : null;
    }
}