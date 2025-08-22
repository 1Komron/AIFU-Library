package aifu.project.libraryweb.service.student_service;


import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import aifu.project.libraryweb.service.student_service.utel_service.StudentExcelReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Talabalarni deaktivatsiya qilish uchun asosiy servis.
 * Bu implementatsiya kod takrorlanishining oldini olish uchun Exceldan o'qish vazifasini
 * alohida `StudentExcelReaderService`ga yuklaydi va o'zi faqat asosiy biznes mantiq
 * (qarzdorlikni tekshirish, deaktivatsiya qilish, hisobot yaratish) bilan shug'ullanadi.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDeactivationServiceImpl implements StudentDeactivationService {

    // --- BOG'LIQLIKLAR (DEPENDENCIES) ---
    private final StudentRepository studentRepository;
    private final PassportHasher passportHasher;
    private final BookingRepository bookingRepository;
    private final DebtorReportExcelService debtorReportExcelService;
    private final ImportErrorReportExcelService importErrorReportExcelService;
    private final StudentExcelReaderService studentExcelReaderService; // <-- Yangi yordamchi servis

    @Override
    @Transactional
    public DeactivationStats deactivateStudents(InputStream inputStream) {
        log.info("Talabalarni deaktivatsiya qilish jarayoni boshlandi.");

        // 1-QADAM: EXCELDAN MA'LUMOTLARNI YORDAMCHI SERVIS ORQALI O'QISH
        // Endi barcha murakkab o'qish logikasi shu bitta metod chaqiruvi ortida yashiringan.
        List<Student> studentsFromExcel = studentExcelReaderService.readFullStudentDataFromExcel(inputStream);

        if (studentsFromExcel.isEmpty()) {
            log.warn("Deaktivatsiya to'xtatildi: Excel faylda qayta ishlanadigan ma'lumot topilmadi.");
            return new DeactivationStats(0, Collections.emptyList(), Collections.emptyList());
        }
        log.info("Exceldan deaktivatsiya uchun {} ta yozuv o'qildi. Baza bilan solishtirish boshlanmoqda...", studentsFromExcel.size());

        // 2-QADAM: BAZADAN BARCHA POTENSIAL NOMZODLARNI OLISH
        Set<String> plainPassports = studentsFromExcel.stream().map(Student::getPassportCode).collect(Collectors.toSet());
        Set<String> hashedPassports = plainPassports.stream().map(passportHasher::hash).collect(Collectors.toSet());
        List<Student> allFoundStudentsInDb = studentRepository.findByPassportCodeInAndIsDeletedFalse(hashedPassports);
        Map<String, Student> dbStudentMap = allFoundStudentsInDb.stream().collect(Collectors.toMap(Student::getPassportCode, s -> s));

        // 3-QADAM: TOPILGANLAR ICHIDAN QARZDORLARNI ANIQLASH
        Set<Long> foundStudentIds = allFoundStudentsInDb.stream().map(Student::getId).collect(Collectors.toSet());
        // Agar topilgan talabalar bo'lmasagina bazaga murojaat qilamiz, aks holda bo'sh to'plam qaytaradi.
        Set<Long> debtorStudentIds = foundStudentIds.isEmpty() ? Collections.emptySet() : bookingRepository.findStudentIdsWithActiveBookings(foundStudentIds);
        log.info("{} ta topilgan talaba ichidan {} tasi qarzdor deb topildi.", foundStudentIds.size(), debtorStudentIds.size());

        // 4-QADAM: GURUHLARGA AJRATISH (DEAKTIVATSIYA QILINADIGANLAR, QARZDORLAR, TOPILMAGANLAR)
        List<Student> studentsToDeactivate = new ArrayList<>();
        List<DebtorInfoDTO> debtors = new ArrayList<>();
        List<ImportErrorDTO> notFoundRecords = new ArrayList<>();

        for (Student studentFromExcel : studentsFromExcel) {
            String hashedPassport = passportHasher.hash(studentFromExcel.getPassportCode());

            if (dbStudentMap.containsKey(hashedPassport)) {
                // TALABA BAZADAN TOPILDI
                Student studentInDb = dbStudentMap.get(hashedPassport);
                if (debtorStudentIds.contains(studentInDb.getId())) {
                    debtors.add(new DebtorInfoDTO(
                            studentInDb.getId(),
                            studentInDb.getName(),
                            studentInDb.getSurname(),
                            studentInDb.getFaculty()
                    ));
                } else {
                    // DEAKTIVATSIYA QILINADIGANLAR GURUHI
                    studentsToDeactivate.add(studentInDb);
                }
            } else {
                // TALABA BAZADAN TOPILMADI
                notFoundRecords.add(new ImportErrorDTO(
                        studentFromExcel.getName(), studentFromExcel.getSurname(), studentFromExcel.getDegree(),
                        studentFromExcel.getFaculty(), studentFromExcel.getAdmissionTime(), studentFromExcel.getGraduationTime(),
                        "Talaba ma'lumotlar bazasida topilmadi yoki allaqachon deaktivatsiya qilingan."
                ));
            }
        }

        // 5-QADAM: O'CHIRISH OPERATSIYASI
        if (!studentsToDeactivate.isEmpty()) {
            studentsToDeactivate.forEach(s -> {
                s.setDeleted(true);
                s.setActive(false);
            });
            studentRepository.saveAll(studentsToDeactivate);
            log.info("{} ta talaba muvaffaqiyatli deaktivatsiya qilindi.", studentsToDeactivate.size());
        }

        // 6-QADAM: YAKUNIY HISOBOT OBYEKTINI YARATISH
        DeactivationStats stats = new DeactivationStats(studentsToDeactivate.size(), debtors, notFoundRecords);

        // 7-QADAM: KERAK BO'LSA, EXCEL-HISOBOTLARNI GENERATSIYA QILISH
        generateDebtorReport(stats, debtors);
        generateNotFoundReport(stats, notFoundRecords);

        log.info("Deaktivatsiya jarayoni yakunlandi. Muvaffaqiyatli: {}, Qarzdorlar: {}, Topilmaganlar: {}", stats.getSuccessCount(), stats.getDebtors().size(), stats.getNotFoundRecords().size());
        return stats;
    }

    /**
     * Agar qarzdorlar bo'lsa, ular uchun Excel hisobotini generatsiya qiladi.
     */
    private void generateDebtorReport(DeactivationStats stats, List<DebtorInfoDTO> debtors) {
        if (!debtors.isEmpty()) {
            try {
                byte[] excelFile = debtorReportExcelService.genereateDebtorReport(debtors);
                String fileName = "qarzdor_talabalar_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
                stats.setDebtorsReportFileName(fileName);
                stats.setDebtorsReportFileBase64(Base64.getEncoder().encodeToString(excelFile));
                log.info("{} nomli qarzdorlar hisoboti generatsiya qilindi.", fileName);
            } catch (Exception e) {
                log.error("Qarzdorlar hisobotini generatsiya qilishda xatolik yuz berdi", e);
            }
        }
    }

    /**
     * Agar bazadan topilmagan yozuvlar bo'lsa, ular uchun Excel hisobotini generatsiya qiladi.
     */
    private void generateNotFoundReport(DeactivationStats stats, List<ImportErrorDTO> notFoundRecords) {
        if (!notFoundRecords.isEmpty()) {
            try {
                byte[] excelFile = importErrorReportExcelService.generateStudentImportErrorReport(notFoundRecords);
                String fileName = "topilmagan_talabalar_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
                stats.setNotFoundReportFileName(fileName);
                stats.setNotFoundReportFileBase64(Base64.getEncoder().encodeToString(excelFile));
                log.info("{} nomli topilmagan talabalar hisoboti generatsiya qilindi.", fileName);
            } catch (IOException e) {
                log.error("Topilmagan talabalar hisobotini generatsiya qilishda xatolik yuz berdi", e);
            }
        }
    }
}