package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.StudentImportException;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDeactivationServiceImpl implements StudentDeactivationService {

    private final StudentRepository studentRepository;
    private final BookingRepository bookingRepository;
    private final PassportHasher passportHasher;


@Override
@Transactional
    public DeactivationStats deactivateStudents(InputStream inputStream) {
        // 1-QADAM: Excel fayldan pasport kodlarini o'qib olish.
        Set<String> plainPassportCodesFromExcel = readPassportCodesFromExcel(inputStream);

        if (plainPassportCodesFromExcel.isEmpty()) {
            log.warn("Deaktivatsiya uchun yuklangan Excel fayl bo'sh yoki unda pasport kodlari topilmadi.");
            // Bu holatda exception tashlash o'rniga, statistikada ma'lumot berish to'g'riroq,
            // chunki bu tizim xatosi emas, balki bo'sh fayl holati.
            return new DeactivationStats(0, List.of("Excel faylda pasport kodlari topilmadi."));
        }
        log.info("Exceldan {} ta noyob pasport kodi o'qildi. Deaktivatsiya jarayoni boshlanmoqda...", plainPassportCodesFromExcel.size());

        // 2-QADAM: Pasport kodlarini xeshlash.
        Map<String, String> hashedToPlainMap = plainPassportCodesFromExcel.stream()
                .collect(Collectors.toMap(passportHasher::hash, plain -> plain, (p1, p2) -> p1));
        log.debug("Xeshlangan pasport kodlari: {}", hashedToPlainMap.keySet());

        // 3-QADAM: Bazadan mos talabalarni topish.
        List<Student> studentsToDeactivate = studentRepository.findByPassportCodeInAndIsDeletedFalse(hashedToPlainMap.keySet());
        log.info("Ma'lumotlar bazasidan {} ta mos keladigan talaba topildi.", studentsToDeactivate.size());

        // Agar hech kim topilmasa, hisobotni qaytaramiz.
        if (studentsToDeactivate.isEmpty()) {
            List<String> notFoundErrors = plainPassportCodesFromExcel.stream()
                    .map(plain -> "Talaba (Pasport: " + plain + ") ma'lumotlar bazasida topilmadi.")
                    .toList();
            return new DeactivationStats(0, notFoundErrors);
        }

        // 4-QADAM: Qarzdorlikni tekshirish.
        Set<Long> studentIds = studentsToDeactivate.stream().map(Student::getId).collect(Collectors.toSet());
        Set<Long> studentsWithDebt = bookingRepository.findStudentIdsWithActiveBookings(studentIds);
        log.info("{} ta talabada kitobdan qarzdorlik aniqlandi.", studentsWithDebt.size());

        // 5-QADAM: Yakuniy qaror va hisobotni shakllantirish.
        List<Student> studentsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Student student : studentsToDeactivate) {
            if (studentsWithDebt.contains(student.getId())) {
                String errorMsg = String.format("Talaba (ID: %d, F.I.Sh: %s %s) kitobdan qarzdorligi borligi uchun o'chirilmadi.",
                        student.getId(), student.getSurname(), student.getName());
                errors.add(errorMsg);
                log.warn(errorMsg);
            } else {
                student.setDeleted(true);
                student.setActive(false);
                studentsToSave.add(student);
                successCount++;
            }
        }

        Set<String> foundHashedPassports = studentsToDeactivate.stream()
                .map(Student::getPassportCode).collect(Collectors.toSet());
        hashedToPlainMap.keySet().stream()
                .filter(hashed -> !foundHashedPassports.contains(hashed))
                .forEach(hashed -> errors.add("Talaba (Pasport: " + hashedToPlainMap.get(hashed) + ") ma'lumotlar bazasida topilmadi."));

        // 6-QADAM: O'zgarishlarni saqlash.
        if (!studentsToSave.isEmpty()) {
            studentRepository.saveAll(studentsToSave);
            log.info("{} ta talaba muvaffaqiyatli deaktivatsiya qilindi.", successCount);
        }

        return new DeactivationStats(successCount, errors);
    }


    private Set<String> readPassportCodesFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new StudentImportException("Excel fayl formati noto'g'ri: sarlavha qatori (birinchi qator) mavjud emas.");
            }

            int passportColumnIndex = -1;
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;
                String headerValue = dataFormatter.formatCellValue(cell).trim().toLowerCase();
                if (headerValue.contains("image") || headerValue.contains("pasport")) {
                    passportColumnIndex = cell.getColumnIndex();
                    log.info("Pasport ma'lumotlari uchun ustun '{}' (indeks: {}) tanlandi.", dataFormatter.formatCellValue(cell), passportColumnIndex);
                    break;
                }
            }

            if (passportColumnIndex == -1) {
                throw new StudentImportException("Pasport kodi ustuni topilmadi. Iltimos, Excel fayldagi ustun nomida " +
                        "'passport' yoki 'pasport' so'zi borligiga ishonch hosil qiling.");
            }

            Set<String> passportCodes = new HashSet<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(passportColumnIndex);
                    if (cell != null) {
                        String passportCode = dataFormatter.formatCellValue(cell).trim();
                        if (!passportCode.isBlank()) {
                            passportCodes.add(passportCode);
                        }
                    }
                }
            }
            return passportCodes;
        } catch (StudentImportException e) {
            log.warn("Talabalarni import qilishda mantiqiy xato: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Excel faylni o'qishda texnik xatolik yuz berdi.", e);
            throw new StudentImportException("Excel faylni o'qishda kutilmagan texnik xatolik yuz berdi. Fayl formati to'g'riligini tekshiring.", e);
        }
    }


}