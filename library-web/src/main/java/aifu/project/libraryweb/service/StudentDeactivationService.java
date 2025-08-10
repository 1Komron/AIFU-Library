package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.common_domain.entity.Student;
import aifu.project.libraryweb.repository.BookingRepository;
import aifu.project.libraryweb.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bu servis Excel fayl orqali talabalarni ommaviy ravishda "yumshoq o'chirish" (deaktivatsiya qilish)
 * uchun mo'ljallangan. U pasport kodlarini xavfsiz xeshlash mantig'ini to'liq qo'llab-quvvatlaydi.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDeactivationService {

    private final StudentRepository studentRepository;
    private final BookingRepository bookingRepository;
    private final PassportHasher passportHasher; // HASHER'NI INJECT QILAMIZ

    /**
     * Berilgan Excel fayldagi pasport kodlari bo'yicha talabalarni deaktivatsiya qiladi.
     * Jarayon xavfsizlik va unumdorlik uchun optimallashtirilgan.
     */
    @Transactional
    public DeactivationStats deactivateStudentsFromExcel(InputStream inputStream) {
        // 1-QADAM: Excel fayldan ochiq matndagi pasport kodlarini o'qib olamiz.
        Set<String> plainPassportCodesFromExcel = readPassportCodesFromExcel(inputStream);
        if (plainPassportCodesFromExcel.isEmpty()) {
            log.warn("Deaktivatsiya uchun yuklangan Excel fayl bo'sh yoki unda pasport kodlari topilmadi.");
            return new DeactivationStats(0, List.of("Excel faylda pasport kodlari topilmadi."));
        }
        log.info("Exceldan {} ta noyob pasport kodi o'qildi. Deaktivatsiya jarayoni boshlanmoqda...", plainPassportCodesFromExcel.size());

        // 2-QADAM: O'qilgan barcha ochiq matndagi kodlarni xeshlaymiz.
        // Bu bizga bazadagi xeshlangan qiymatlar bilan solishtirish imkonini beradi.
        Map<String, String> hashedToPlainMap = plainPassportCodesFromExcel.stream()
                .collect(Collectors.toMap(
                        passportHasher::hash, // Kalit (Key) - bu xeshlangan kod
                        plain -> plain,       // Qiymat (Value) - bu ochiq matndagi kod
                        (p1, p2) -> p1        // Agar bir xil xesh bo'lsa (ehtimoli yo'q), birinchisini olamiz
                ));
        log.debug("Xeshlangan pasport kodlari: {}", hashedToPlainMap.keySet());

        // 3-QADAM: XESHLANGAN kodlar bo'yicha bazadan mos keladigan talabalarni BITTADA topamiz.
        // Bu "cursor error"ning oldini oladi va juda tez ishlaydi.
        List<Student> studentsToDeactivate = studentRepository.findByPassportCodeInAndIsDeletedFalse(hashedToPlainMap.keySet());
        log.info("Ma'lumotlar bazasidan {} ta mos keladigan talaba topildi.", studentsToDeactivate.size());

        // Agar hech kim topilmasa, darhol natijani qaytaramiz.
        if (studentsToDeactivate.isEmpty()) {
            List<String> notFoundErrors = plainPassportCodesFromExcel.stream()
                    .map(plain -> "Talaba (Pasport: " + plain + ") ma'lumotlar bazasida topilmadi.")
                    .toList();
            return new DeactivationStats(0, notFoundErrors);
        }

        Set<Long> studentIds = studentsToDeactivate.stream().map(Student::getId).collect(Collectors.toSet());

        // 4-QADAM: Topilgan talabalarning kitobdan qarzdorligini BITTADA tekshiramiz.
        Set<Long> studentsWithDebt = bookingRepository.findStudentIdsWithActiveBookings(studentIds);
        log.info("{} ta talabada kitobdan qarzdorlik aniqlandi.", studentsWithDebt.size());

        List<Student> studentsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        // 5-QADAM: Har bir topilgan talaba uchun yakuniy qaror qabul qilamiz.
        for (Student student : studentsToDeactivate) {
            if (studentsWithDebt.contains(student.getId())) {
                // Agar talabaning qarzi bo'lsa, uni o'chirmaymiz.
                String errorMsg = String.format("Talaba (ID: %d, F.I.Sh: %s %s) kitobdan qarzdorligi borligi uchun o'chirilmadi.",
                        student.getId(), student.getSurname(), student.getName());
                errors.add(errorMsg);
                log.warn(errorMsg);
            } else {
                // Agar qarzi bo'lmasa, uni "yumshoq o'chiramiz".
                student.setDeleted(true);
                student.setActive(false);
                studentsToSave.add(student);
                successCount++;
            }
        }

        // Excelda bor, lekin bazada topilmagan talabalarni ham hisobotga qo'shamiz.
        Set<String> foundHashedPassports = studentsToDeactivate.stream()
                .map(Student::getPassportCode)
                .collect(Collectors.toSet());

        hashedToPlainMap.keySet().stream()
                .filter(hashed -> !foundHashedPassports.contains(hashed))
                .forEach(hashed -> errors.add("Talaba (Pasport: " + hashedToPlainMap.get(hashed) + ") ma'lumotlar bazasida topilmadi."));

        // 6-QADAM: Faqat o'zgartirilgan talabalarni bitta so'rov bilan bazaga saqlaymiz.
        if (!studentsToSave.isEmpty()) {
            studentRepository.saveAll(studentsToSave);
            log.info("{} ta talaba muvaffaqiyatli deaktivatsiya qilindi.", successCount);
        }

        // Yakuniy hisobotni qaytaramiz.
        return new DeactivationStats(successCount, errors);
    }

    /**
     * Excel fayldan faqat birinchi ustunni (pasport kodini) o'qiydigan yordamchi metod.
     */
    private Set<String> readPassportCodesFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Set<String> passportCodes = new HashSet<>();
            // Sarlavhadan keyingi qatordan boshlaymiz (i=1)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    String passportCode = new DataFormatter().formatCellValue(row.getCell(0)).trim();
                    if (!passportCode.isBlank()) {
                        passportCodes.add(passportCode);
                    }
                }
            }
            return passportCodes;
        } catch (Exception e) {
            log.error("Bitiruvchilarning Excel faylini o'qishda xatolik yuz berdi.", e);
            throw new RuntimeException("Excel faylni o'qishda xatolik yuz berdi.");
        }
    }
}