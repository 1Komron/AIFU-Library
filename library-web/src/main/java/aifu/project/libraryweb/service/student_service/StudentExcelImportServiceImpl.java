package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.StudentImportException;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import aifu.project.libraryweb.service.student_service.utel_service.StudentExcelReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class StudentExcelImportServiceImpl implements StudentExcelImportService {

    // --- BOG'LIQLIKLAR (DEPENDENCIES) ---
    private final StudentRepository studentRepository;
    private final PassportHasher passportHasher;
    private final TransactionTemplate transactionTemplate;
    private final ImportErrorReportExcelService importErrorReportExcelService;
    private final StudentExcelReaderService studentExcelReaderService; // <-- Yangi yordamchi servis

    /**
     * {@inheritDoc}
     */
    @Override
    public ImportStats importStudentsFromExcel(InputStream inputStream) {
        log.info("Talabalarni Exceldan import qilish jarayoni boshlandi.");

        try {
            // 1-QADAM: EXCELDAN MA'LUMOTLARNI YORDAMCHI SERVIS ORQALI O'QISH
            // Barcha murakkab o'qish logikasi shu bitta metod chaqiruvi ortida yashiringan.
            List<Student> studentsFromExcel = studentExcelReaderService.readFullStudentDataFromExcel(inputStream);

            if (studentsFromExcel.isEmpty()) {
                log.warn("Import to'xtatildi: Excel faylda qayta ishlanadigan ma'lumot topilmadi.");
                return new ImportStats(0, Collections.emptyList());
            }
            log.info("Excel fayldan {} ta yozuv o'qildi. Bazaga saqlash boshlanmoqda...", studentsFromExcel.size());

            // 2-QADAM: O'qilgan talabalarni birma-bir qayta ishlab, bazaga saqlash va hisobot yaratish
            return processAndSaveIndividually(studentsFromExcel);

        } catch (StudentImportException e) {
            // StudentExcelReaderService dan kelgan xatoliklarni o'zgartirmasdan tashqariga uzatamiz
            throw e;
        } catch (Exception e) {
            // Boshqa kutilmagan xatoliklar uchun umumiy xabar
            log.error("Import jarayonida kutilmagan texnik xatolik yuz berdi", e);
            throw new StudentImportException("Import jarayonida kutilmagan texnik xatolik: " + e.getMessage(), e);
        }
    }

    /**
     * Talabalar ro'yxatini birma-bir aylanib chiqib, har birini alohida saqlaydi
     * va jarayon natijalari bo'yicha to'liq hisobot shakllantiradi.
     * @param allStudents Exceldan o'qilgan barcha talabalar ro'yxati.
     * @return Jarayon statistikasi bilan to'ldirilgan ImportStats obyekti.
     */
    private ImportStats processAndSaveIndividually(List<Student> allStudents) {
        int successCount = 0;
        List<ImportErrorDTO> failedRecords = new ArrayList<>();
        Set<String> processedPlainPassports = new HashSet<>(); // Excel ichidagi dublikatlarni aniqlash uchun

        for (Student student : allStudents) {
            String plainPassport = student.getPassportCode();
            String errorReason = null;

            // 1. Excel faylning o'zidagi takrorlanishlarni tekshirish
            if (!processedPlainPassports.add(plainPassport)) {
                errorReason = "Excel faylning o'zida takrorlangan.";
            } else {
                // 2. Pasport kodini faqat saqlashdan oldin xeshlaymiz
                student.setPassportCode(passportHasher.hash(plainPassport));
                // 3. Yangi talabalar uchun standart qiymatlarni belgilaymiz
                student.setRole(Role.STUDENT);
                student.setActive(true);
                student.setDeleted(false);

                try {
                    // 4. Har bir saqlashni alohida kichik tranzaksiyada bajarish
                    transactionTemplate.execute(status -> {
                        studentRepository.save(student);
                        return null;
                    });
                    successCount++;
                } catch (DataIntegrityViolationException e) {
                    // 5. BAZA XATOSI: UNIQUE cheklovi buzildi (Partial Index ishga tushdi)
                    log.warn("Dublikat yozuv aniqlandi (Pasport: {}). Sabab: {}", plainPassport, e.getMostSpecificCause().getMessage());
                    if (e.getMostSpecificCause().getMessage().contains("student_passport_code")) {
                        errorReason = "Bu pasport kodli aktiv talaba allaqachon mavjud.";
                    } else if (e.getMostSpecificCause().getMessage().contains("student_card_number")) {
                        errorReason = "Bu karta raqamli aktiv talaba allaqachon mavjud.";
                    } else {
                        errorReason = "Noma'lum takrorlanish xatosi.";
                    }
                } catch (Exception e) {
                    // 6. Boshqa kutilmagan xatolar
                    log.error("Talabani saqlashda kutilmagan xato (Pasport: {})", plainPassport, e);
                    errorReason = "Kutilmagan tizim xatoligi.";
                }
            }

            // 7. Agar biror bir xatolik yuz bergan bo'lsa, uni hisobot uchun ro'yxatga qo'shamiz
            if (errorReason != null) {
                failedRecords.add(new ImportErrorDTO(
                        student.getName(),
                        student.getSurname(),
                        student.getDegree(),
                        student.getFaculty(),
                        student.getAdmissionTime(),
                        student.getGraduationTime(),
                        errorReason
                ));
            }
        }

        // 8. Yakuniy statistikani yaratamiz
        ImportStats stats = new ImportStats(successCount, failedRecords);

        // 9. Agar xatoliklar mavjud bo'lsa, ular uchun Excel hisobot generatsiya qilamiz
        if (!failedRecords.isEmpty()) {
            try {
                byte[] excelFile = importErrorReportExcelService.generateStudentImportErrorReport(failedRecords);
                String fileName = "import_xatoliklari_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

                stats.setReportFileName(fileName);
                stats.setReportFileBase64(Base64.getEncoder().encodeToString(excelFile));
                log.info("{} nomli xatoliklar hisoboti muvaffaqiyatli generatsiya qilindi.", fileName);
            } catch (IOException e) {
                log.error("Import xatoliklari hisobotini Excelga yozishda xatolik!", e);
            }
        }

        log.info("Import jarayoni yakunlandi. Muvaffaqiyatli: {}, Xatolar: {}", successCount, failedRecords.size());
        return stats;
    }


}