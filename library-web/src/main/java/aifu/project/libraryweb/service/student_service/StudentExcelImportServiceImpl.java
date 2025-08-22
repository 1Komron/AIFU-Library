package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;

import aifu.project.libraryweb.service.student_service.utel_service.StudentExcelReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Talabalarni Exceldan import qilish uchun servis.
 * Bu implementatsiya "Partial Unique Index" mantig'iga tayanadi.
 * Yaroqli yozuvlar bitta `saveAll` operatsiyasi bilan saqlanadi va "race condition"
 * kabi parallel so'rovlar keltirib chiqaradigan xatoliklar uchun maxsus himoya mantig'iga ega.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentExcelImportServiceImpl implements StudentExcelImportService {

    private final StudentRepository studentRepository;
    private final PassportHasher passportHasher;
    private final ImportErrorReportExcelService importErrorReportExcelService;
    private final StudentExcelReaderService studentExcelReaderService;

    @Override
    @Transactional
    public ImportStats importStudentsFromExcel(InputStream inputStream) {
        log.info("Import jarayoni boshlandi ('saveAll' bilan, 'race condition' himoyasi).");

        // 1-QADAM: Exceldan barcha talabalarni o'qish
        List<Student> studentsFromExcel = studentExcelReaderService.readFullStudentDataFromExcel(inputStream);
        if (studentsFromExcel.isEmpty()) {
            return new ImportStats(0, Collections.emptyList());
        }

        // 2-QADAM: BAZADAGI PASPORT DUPLIKATLARINI OLDINDAN ANIQLASH
        Set<String> plainPassportsFromExcel = studentsFromExcel.stream().map(Student::getPassportCode).collect(Collectors.toSet());
        Set<String> hashedPassports = plainPassportsFromExcel.stream().map(passportHasher::hash).collect(Collectors.toSet());
        Set<String> existingActiveHashedPassports = studentRepository.findActiveExistingHashedPassportCodesNative(hashedPassports);

        // 3-QADAM: FILTRLASH VA GURUHLARGA AJRATISH
        List<Student> studentsToSave = new ArrayList<>();
        List<ImportErrorDTO> failedRecords = new ArrayList<>();
        Set<String> processedPlainPassports = new HashSet<>();

        for (Student student : studentsFromExcel) {
            String plainPassport = student.getPassportCode();
            String hashedPassport = passportHasher.hash(plainPassport);
            if (!processedPlainPassports.add(plainPassport)) {
                failedRecords.add(createErrorDTO(student, "Excel faylning o'zida takrorlangan."));
                continue;
            }
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

        // =========================================================================================
        // 4-QADAM: OMMBAVIY SAQLASH (`saveAll` bilan, "RACE CONDITION" himoyasi)
        // =========================================================================================
        /**
         * Bu blokning vazifasi - oldindan tekshiruvdan o'tgan "toza" ro'yxatni
         * bitta `saveAll` operatsiyasi bilan saqlash. Agar shu qisqa vaqt ichida
         * boshqa bir parallel so'rov tufayli dublikat paydo bo'lib qolsa ("race condition"),
         * `DataIntegrityViolationException` yuz beradi. Biz bu xatoni ushlab,
         * aynan qaysi yozuv muammo tug'dirganini aniqlaymiz, uni xatolar ro'yxatiga
         * o'tkazamiz va qolganlarini qayta saqlashga urinamiz.
         */
        int savedCount = 0;
        if (!studentsToSave.isEmpty()) {
            List<Student> toSaveBatch = new ArrayList<>(studentsToSave); // Asl ro'yxatni buzmaslik uchun nusxa olamiz
            boolean shouldRetry = true;
            while (shouldRetry && !toSaveBatch.isEmpty()) {
                shouldRetry = false; // Qayta urinishni to'xtatamiz, agar xato bo'lmasa
                try {
                    studentRepository.saveAll(toSaveBatch);
                    savedCount += toSaveBatch.size();
                    log.info("{} ta yangi talaba to'plamda muvaffaqiyatli saqlandi.", toSaveBatch.size());
                    toSaveBatch.clear(); // Barcha yozuvlar saqlandi
                } catch (DataIntegrityViolationException e) {
                    log.warn("Ommaviy saqlashda ziddiyat (ehtimoliy race condition): {}", e.getMostSpecificCause().getMessage());
                    // Xatoning matnidan aynan qaysi pasport xato berganini ajratib olamiz
                    String duplicateHash = extractDuplicateHash(e.getMostSpecificCause().getMessage());
                    if (duplicateHash != null) {
                        Iterator<Student> iterator = toSaveBatch.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                            Student s = iterator.next();
                            if (s.getPassportCode().equals(duplicateHash)) {
                                failedRecords.add(createErrorDTO(s, "Aktiv dublikat (parallel so'rov tufayli aniqlandi)."));
                                iterator.remove(); // Xatolikni "toza" ro'yxatdan olib tashlaymiz
                                shouldRetry = true;  // Ro'yxat o'zgargani uchun, qolganini qayta saqlashga urinib ko'ramiz
                                log.info("Race condition'da aniqlangan xato ro'yxatdan olib tashlandi. Qolgan {} ta yozuv qayta saqlanadi.", toSaveBatch.size());
                                found = true;
                                break;
                            }
                        }
                        if (!found) shouldRetry = false; // Agar xatodagi xesh ro'yxatda topilmasa, tsiklni to'xtatamiz
                    } else {
                        log.error("Ommaviy saqlashda pasportni aniqlab bo'lmaydigan dublikat xatosi!");
                        toSaveBatch.forEach(s -> failedRecords.add(createErrorDTO(s, "Noma'lum dublikat xatosi (ommaviy saqlashda).")));
                        toSaveBatch.clear();
                    }
                }
            }
        }

        // 5-QADAM: YAKUNIY HISOBOTNI SHAKLLANTIRISH
        ImportStats stats = new ImportStats(savedCount, failedRecords);
        if (!failedRecords.isEmpty()) {
            try {
                byte[] excelFile = importErrorReportExcelService.generateStudentImportErrorReport(failedRecords);
                String fileName = "import_xatoliklari_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
                stats.setReportFileName(fileName);
                stats.setReportFileBase64(Base64.getEncoder().encodeToString(excelFile));
                log.info("{} nomli xatoliklar hisoboti generatsiya qilindi.", fileName);
            } catch (IOException e) {
                log.error("Import xatoliklari hisobotini Excelga yozishda xatolik!", e);
            }
        }

        log.info("Import jarayoni yakunlandi. Muvaffaqiyatli: {}, Xatolar: {}", stats.getSuccessCount(), stats.getFailedRecords().size());
        return stats;
    }

    /**
     * Xatolik DTO'sini yaratish uchun yordamchi metod.
     */
    private ImportErrorDTO createErrorDTO(Student student, String reason) {
        return new ImportErrorDTO(
                student.getName(),
                student.getSurname(),
                student.getDegree(),
                student.getFaculty(),
                student.getAdmissionTime(),
                student.getGraduationTime(),
                reason
        );
    }

    /**
     * PostgreSQL'ning xatolik matnidan dublikat bo'lgan pasport kodini
     * (xeshlangan holatda) ajratib oladigan yordamchi metod.
     * @param message `e.getMostSpecificCause().getMessage()` dan olingan matn.
     * @return Xeshlangan pasport kodi yoki topilmasa `null`.
     */
    private String extractDuplicateHash(String message) {
        if (message == null) return null;
        Pattern pattern = Pattern.compile("\\(passport_code\\)=\\((.*?)\\)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }
}