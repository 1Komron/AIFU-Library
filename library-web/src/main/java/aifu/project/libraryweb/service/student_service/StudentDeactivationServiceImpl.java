package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.StudentImportException;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
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
 * StudentDeactivationService interfeysining yakuniy, optimallashtirilgan va soddalashtirilgan implementatsiyasi.
 * Bu klass Excel fayldagi talabalarni ommaviy deaktivatsiya qilish uchun mo'ljallangan.
 * Mantiq bazaga murojaatlarni minimallashtirishga qaratilgan va Pageable (sahifalash) ishlatmaydi.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDeactivationServiceImpl implements StudentDeactivationService {

    // --- BOG'LIQLIKLAR (DEPENDENCIES) ---
    /**
     * Ma'lumotlar bazasi bilan aloqa qilish uchun StudentRepository.
     * Spring bu obyektni avtomatik ravishda yaratib, servisga taqdim etadi (Dependency Injection).
     */
    private final StudentRepository studentRepository;

    /**
     * Pasport kodlarini xavfsiz xeshlash va tekshirish uchun servis.
     * Bu ham Spring tomonidan inject qilinadi.
     */
    private final PassportHasher passportHasher;

    /**
     * {@inheritDoc}
     * Bu implementatsiya Excel fayldagi pasport kodlari bo'yicha talabalarni deaktivatsiya qiladi.
     * Jarayon optimallashtirilgan: bitta so'rovda ham talabalar topiladi, ham ularning qarzdorligi tekshiriladi.
     * @param inputStream Kontrollerdan kelayotgan Excel faylning oqimi.
     * @return Jarayon natijalari (muvaffaqiyatli va xatolar soni) haqidagi DeactivationStats obyekti.
     */
    @Override
    @Transactional // Bu annotatsiya barcha baza operatsiyalari bitta paketda (tranzaksiya) bo'lishini ta'minlaydi.
    // Ya'ni, yoki barcha talabalar o'zgaradi, yoki bittasida xato bo'lsa, hech biri o'zgarmaydi.
    public DeactivationStats deactivateStudents(InputStream inputStream) {

        // =========================================================================================
        // 1-QADAM: EXCEL FAYLNI O'QISH (BAZAGA MUROJAAT YO'Q)
        // Maqsad: Yuklangan Excel fayldan barcha pasport kodlarini "xom" holatda o'qib olish.
        // Natija: `Set<String>` ichida fayldagi barcha takrorlanmas pasport kodlari.
        //------------------------------------------------------------------------------------------
        Set<String> plainPassportCodesFromExcel = readPassportCodesFromExcel(inputStream);

        // Agar fayl bo'sh bo'lsa yoki unda umuman kod topilmasa, ishni shu yerda to'xtatamiz.
        if (plainPassportCodesFromExcel.isEmpty()) {
            return new DeactivationStats(0, List.of("Excel faylda pasport kodlari topilmadi."));
        }
        log.info("Exceldan {} ta noyob pasport kodi o'qildi. Deaktivatsiya jarayoni boshlanmoqda...", plainPassportCodesFromExcel.size());


        // =========================================================================================
        // 2-QADAM: KODLARNI XESHLASH (BAZAGA MUROJAAT YO'Q)
        // Maqsad: Xavfsizlik uchun ochiq matndagi kodlarni bazadagi formatga o'tkazish.
        // Natija: `Map` ko'rinishida ma'lumot. Kalit -> xeshlangan kod, Qiymat -> asl kod.
        // Asl kodni saqlab qolishimiz sababi - keyin xatolik xabarida foydalanuvchiga ko'rsatish uchun.
        //------------------------------------------------------------------------------------------
        Map<String, String> hashedToPlainMap = plainPassportCodesFromExcel.stream()
                .collect(Collectors.toMap(
                        passportHasher::hash, // Kalit (Key)
                        plain -> plain,       // Qiymat (Value)
                        (p1, p2) -> p1        // Agar bir xil xesh bo'lsa, birinchisini olamiz
                ));



        // =========================================================================================
        // 3-QADAM: ASOSIY SO'ROV - "AQLLI ELAK" (BIRINCHI BAZA MUROJAATI - SELECT)
        // Maqsad: Bitta so'rovda ham talabalarni topish, ham ularning qarzdorligi yo'qligini tekshirish.
        // Natija: `List<Student>` - faqatgina "aktiv" va "qarzi yo'q" talabalarning to'liq ro'yxati.
        //------------------------------------------------------------------------------------------
        List<Student> studentsToDeactivate = studentRepository.findNonDebtorStudentsByPassportCodes(hashedToPlainMap.keySet());
        log.info("Bazadan {} ta qarzdorligi yo'q talaba topildi va deaktivatsiyaga tayyor.", studentsToDeactivate.size());


        // =========================================================================================
        // 4-QADAM: HISOBOT UCHUN XATOLARNI ANIQLASH (BAZAGA MUROJAAT YO'Q)
        // Maqsad: Excelda bor, lekin bazadan topilmagan (yoki qarzdor bo'lgani uchun "elakdan o'tmagan")
        // talabalarning ro'yxatini shakllantirish.
        //------------------------------------------------------------------------------------------
        List<String> errors = new ArrayList<>();

        // Avval, haqiqatan ham topilgan talabalarning xeshlangan kodlarini bir to'plamga yig'ib olamiz.
        Set<String> foundHashedPassports = studentsToDeactivate.stream()
                .map(Student::getPassportCode)
                .collect(Collectors.toSet());

        // Endi Exceldan olingan barcha kodlar xaritasini (`hashedToPlainMap`) aylanib chiqamiz.
        // Agar xaritadagi biror kod "topilganlar ro'yxati"da bo'lmasa, demak u xato.
        hashedToPlainMap.forEach((hashed, plain) -> {
            if (!foundHashedPassports.contains(hashed)) {
                // Xatolik xabarini asl (ochiq matndagi) pasport kodi bilan yaratamiz.
                errors.add("Talaba (Pasport: " + plain + ") ma'lumotlar bazasida topilmadi yoki kitobdan qarzdorligi mavjud.");
            }
        });


        // =========================================================================================
        // 5-QADAM: O'ZGARTIRISH VA SAQLASH (IKKINCHI BAZA MUROJAATI - UPDATE)
        // Maqsad: "Elakdan o'tgan" barcha talabalarning holatini o'zgartirish va bu o'zgarishlarni bazaga yozish.
        //------------------------------------------------------------------------------------------
        if (!studentsToDeactivate.isEmpty()) {
            // Ro'yxatdagi har bir talaba obyektining holatini xotirada o'zgartiramiz.
            studentsToDeactivate.forEach(student -> {
                student.setDeleted(true);
                student.setActive(false);
            });

            // Barcha o'zgartirilgan obyektlarni bitta komanda bilan bazaga yuboramiz.
            // JPA aqlli ishlaydi: u bu obyektlarni `UPDATE` qiladi, chunki ularning ID'si bor.
            studentRepository.saveAll(studentsToDeactivate);
            log.info("{} ta talaba muvaffaqiyatli deaktivatsiya qilindi.", studentsToDeactivate.size());
        }


        // =========================================================================================
        // 6-QADAM: YAKUNIY HISOBOTNI QAYTARISH
        // Maqsad: Jarayon natijalarini (muvaffaqiyatli o'chirilganlar soni va xatolar ro'yxati)
        // foydalanuvchiga ko'rsatish uchun qaytarish.
        //------------------------------------------------------------------------------------------
        return new DeactivationStats(studentsToDeactivate.size(), errors);
    }

    /**
     * Bu yordamchi metodning vazifasi faqat Excel fayldan ma'lumot o'qish.
     * U pasport ustunini avtomatik topadi va kodlarni yig'ib beradi.
     * @param inputStream Kiruvchi Excel fayl oqimi.
     * @return Noyob pasport kodlari to'plami.
     * @throws StudentImportException Agar fayl formati xato bo'lsa yoki kerakli ustun topilmasa.
     */
    private Set<String> readPassportCodesFromExcel(InputStream inputStream) {
        // try-with-resources bloki workbook'ni avtomatik yopilishini ta'minlaydi.
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new StudentImportException("Excel fayl formati noto'g'ri: sarlavha qatori (birinchi qator) mavjud emas.");
            }

            // Sarlavhadan "pasport" ustunini avtomatik qidirish
            int passportColumnIndex = -1;
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;
                String headerValue = dataFormatter.formatCellValue(cell).trim().toLowerCase();
                if (headerValue.contains("passport") || headerValue.contains("pasport")) {
                    passportColumnIndex = cell.getColumnIndex();
                    break; // Topildi, boshqa qidirish shart emas
                }
            }

            // Agar ustun topilmasa, xatolik beramiz
            if (passportColumnIndex == -1) {
                throw new StudentImportException("Pasport kodi ustuni topilmadi. Iltimos, Excel fayldagi ustun nomida 'passport' yoki 'pasport' so'zi borligiga ishonch hosil qiling.");
            }

            Set<String> passportCodes = new HashSet<>();
            // Sarlavhadan keyingi qatordan boshlab o'qish (i = 1)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(passportColumnIndex);
                    if (cell != null) {
                        String passportCode = dataFormatter.formatCellValue(cell).trim();
                        // Bo'sh katakchalarni e'tiborsiz qoldiramiz
                        if (!passportCode.isBlank()) {
                            passportCodes.add(passportCode);
                        }
                    }
                }
            }
            return passportCodes;
        } catch (StudentImportException e) {
            // O'zimiz bergan xatoliklarni o'zgartirmasdan tashqariga uzatamiz
            log.warn("Talabalarni import qilishda mantiqiy xato: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Boshqa kutilmagan xatoliklarni (masalan, fayl buzilgan) o'rab, tushunarli xabar beramiz
            log.error("Excel faylni o'qishda texnik xatolik yuz berdi.", e);
            throw new StudentImportException("Excel faylni o'qishda kutilmagan texnik xatolik yuz berdi. Fayl formati to'g'riligini tekshiring.", e);
        }
    }
}