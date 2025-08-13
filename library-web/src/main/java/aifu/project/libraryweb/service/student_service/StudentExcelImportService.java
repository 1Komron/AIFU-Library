package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.config.ImporterColumnProperties;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StudentExcelImportService {
    private final StudentRepository studentRepository;
    private final ImporterColumnProperties importerColumnProperties;
    private final PassportHasher passportHasher;



    /**
     * Bizning ichki, o'zgarmas "tilimiz".
     * Bu enum kod ichidagi "surname" kabi matnlarni takror yozishdan saqlaydi va
     * dasturni sarlavhalarni bizning ichki tushunchalarimizga bog'lash uchun ishlatiladi.
     */
    private enum Field {
        PASSPORT_CODE, SURNAME, NAME, DEGREE, FACULTY, CARD_NUMBER
    }

    /**
     * Konstruktor orqali Dependency Injection.
     * Spring bu servisni yaratayotganda, unga kerakli bo'lgan UserRepository va ImporterColumnProperties
     * obyektlarini avtomatik ravishda "inject" qiladi (ta'minlaydi).
     */


    /**
     * Butun import jarayonini boshqaruvchi asosiy metod.
     * @Transactional - bu metoddagi barcha baza operatsiyalari bitta tranzaksiya ichida bo'lishini kafolatlaydi.
     * Ya'ni, yoki barcha yangi foydalanuvchilar saqlanadi, yoki bittasida xato bo'lsa, hech biri saqlanmaydi.
     */
    @Transactional
    public ImportStats importStudentsFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new IllegalArgumentException("Excel fayl bo'sh yoki sarlavha qatori mavjud emas.");
            }

            Map<Field, Integer> fieldColumnMap = createFieldColumnMap(headerRow);
            List<Student> studentsFromExcel = readUsersDynamically(sheet, fieldColumnMap);

            if (studentsFromExcel.isEmpty()) {
                return new ImportStats(0, List.of("Excel faylda import qilinadigan yangi ma'lumot topilmadi."));
            }

            return filterAndSaveUsers(studentsFromExcel);

        } catch (Exception e) {
            throw new RuntimeException("Import jarayonida kutilmagan xatolik: " + e.getMessage(), e);
        }
    }

    /**
     * Exceldan olingan ro'yxatni bazadagi mavjud ma'lumotlar bilan solishtiradi va yakuniy hisobotni shakllantiradi.
     */
    private ImportStats filterAndSaveUsers(List<Student> studentsFromExcel) {
        List<Student> newStudentToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // 1-QADAM: Exceldagi BARCHA ochiq matndagi pasport kodlarini yig'ib olamiz.
        Set<String> plainPassportCodesFromExcel = studentsFromExcel.stream()
                .map(Student::getPassportCode)
                .collect(Collectors.toSet());

        // Agar Excelda umuman pasport kodi bo'lmasa, ishni tugatamiz.
        if (plainPassportCodesFromExcel.isEmpty()) {
            return new ImportStats(0, List.of("Excel faylda yaroqli pasport kodlari topilmadi."));
        }

        // 2-QADAM: Yig'ilgan pasport kodlarini barchasini xeshlaymiz.
        Set<String> hashedCodesFromExcel = plainPassportCodesFromExcel.stream()
                .map(passportHasher::hash)
                .collect(Collectors.toSet());

        // 3-QADAM: BAZAGA FAQAT SHU XESHLARNI YUBORIB, mavjudlarini so'raymiz.
        // Bu bitta, samarali so'rov. "Cursor error"ning oldini oladi.
        Set<String> existingDbHashedPassports = studentRepository.findExistingHashedPassportCodes(hashedCodesFromExcel);

        // 4-QADAM: Endi xotirada, tezkor filtrlashni bajaramiz.
        Set<String> processedPassportCodesInExcel = new HashSet<>(); // Fayl ichidagi dublikatlarni aniqlash uchun
        for (Student student : studentsFromExcel) {
            String plainPassportCode = student.getPassportCode();
            String hashedPassportCode = passportHasher.hash(plainPassportCode);

            // Fayl ichidagi dublikatni tekshirish
            if (!processedPassportCodesInExcel.add(plainPassportCode)) {
                errors.add("Talaba (Pasport: " + plainPassportCode + ") Excel faylida takroran kelgan.");
                continue;
            }

            // Bazadagi dublikatni tekshirish (allaqachon olingan xeshlar ro'yxati bilan)
            if (existingDbHashedPassports.contains(hashedPassportCode)) {
                errors.add("Talaba (Pasport: " + plainPassportCode + ") ma'lumotlar bazasida allaqachon mavjud.");
                continue;
            }

            // Student obyektidagi ochiq matnni uning xeshi bilan almashtiramiz.
            student.setPassportCode(hashedPassportCode);
            newStudentToSave.add(student);
        }

        if (!newStudentToSave.isEmpty()) {
            try {
                studentRepository.saveAll(newStudentToSave);
            } catch (DataIntegrityViolationException e) {
                // Kamdan-kam hollarda, parallel so'rovlar paytida yuz berishi mumkin bo'lgan
                // UNIQUE constraint xatosini (masalan, cardNumber uchun) ushlab olamiz.
                errors.add("Ma'lumotlarni saqlashda ziddiyat yuz berdi (ehtimol, karta raqami takrorlangan). Iltimos, qayta urunib ko'ring.");
                return new ImportStats(0, errors);
            }
        }

        return new ImportStats(newStudentToSave.size(), errors);
    }


    /**
     * Exceldagi ma'lumot qatorlarini o'qiydi. Bu metod ochiq matndagi ma'lumotlarni
     * o'qib, Student obyektlarini yaratadi. Xeshlash keyingi bosqichda bo'ladi.
     */
    private List<Student> readUsersDynamically(Sheet sheet, Map<Field, Integer> fieldColumnMap) {
        List<Student> students = new ArrayList<>();
        int headerRowNum = 0;

        for (int i = headerRowNum + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) break;

            String passportCode = getCellValueAsString(row.getCell(fieldColumnMap.get(Field.PASSPORT_CODE)));
            if (passportCode == null || passportCode.isBlank()) break;

            Student student = new Student();

            // Bu bosqichda biz pasport kodini ochiq matn ko'rinishida saqlab turamiz.
            // U keyinchalik `filterAndSaveUsers` metodida xeshlanadi.
            student.setPassportCode(passportCode);
            student.setSurname(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.SURNAME))));
            student.setName(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.NAME))));

            student.setDegree(getOptionalCellValue(row, fieldColumnMap, Field.DEGREE));
            student.setFaculty(getOptionalCellValue(row, fieldColumnMap, Field.FACULTY));
            student.setCardNumber(getOptionalCellValue(row, fieldColumnMap, Field.CARD_NUMBER));

            student.setRole(Role.STUDENT);
            student.setActive(false);
            student.setDeleted(false);
            students.add(student);
        }
        return students;
    }

    /**
     * Sarlavha qatoridan foydalanib, bizning ichki `Field` enumimiz va ustun indeksidan iborat "xarita" (Map) yaratadi.
     */
    private Map<Field, Integer> createFieldColumnMap(Row headerRow) {
        Map<Field, Integer> map = new HashMap<>();
        Map<String, Field> aliasMap = buildAliasMap();

        for (Cell cell : headerRow) {
            String headerText = getCellValueAsString(cell);
            if (headerText != null && !headerText.isBlank()) {
                String cleanHeader = headerText.trim().toLowerCase();
                if (aliasMap.containsKey(cleanHeader)) {
                    Field field = aliasMap.get(cleanHeader);
                    map.put(field, cell.getColumnIndex());
                }
            }
        }

        // Faylda biz uchun eng muhim bo'lgan ustunlar borligini tekshiramiz.
        if (!map.containsKey(Field.PASSPORT_CODE) || !map.containsKey(Field.SURNAME) || !map.containsKey(Field.NAME)) {
            throw new IllegalArgumentException("Excel faylda majburiy ustunlar (pasport, ism, familiya) topilmadi.");
        }
        return map;
    }

    /**
     * `application.yml`dagi "lug'at"ni o'qib, qidiruv uchun qulay bo'lgan teskari xarita (Map) yaratadi.
     * Masalan: {"surname" -> Field.SURNAME, "last name" -> Field.SURNAME, ...}
     */
    private Map<String, Field> buildAliasMap() {
        Map<String, Field> aliasMap = new HashMap<>();
        addAliases(aliasMap, Field.SURNAME, importerColumnProperties.getSurname());
        addAliases(aliasMap, Field.NAME, importerColumnProperties.getName());
        addAliases(aliasMap, Field.PASSPORT_CODE, importerColumnProperties.getPassportCode());
        addAliases(aliasMap, Field.DEGREE, importerColumnProperties.getDegree());
        addAliases(aliasMap, Field.FACULTY, importerColumnProperties.getFaculty());
        addAliases(aliasMap, Field.CARD_NUMBER, importerColumnProperties.getCardNumber());
        return aliasMap;
    }

    /**
     * Yordamchi metod. Vergul bilan ajratilgan "laqab"lar (aliases) ro'yxatini olib, ularni xaritaga joylaydi.
     */
    private void addAliases(Map<String, Field> map, Field field, String aliases) {
        if (aliases != null) {
            for (String alias : aliases.split(",")) {
                map.put(alias.trim(), field);
            }
        }
    }


    /**
     * Ixtiyoriy maydonlarni xavfsiz o'qish uchun yordamchi metod.
     * Agar Excelda bu maydonga mos ustun bo'lmasa, xatolik bermaydi, shunchaki `null` qaytaradi.
     */
    private String getOptionalCellValue(Row row, Map<Field, Integer> map, Field field) {
        Integer index = map.get(field);
        return (index == null) ? null : getCellValueAsString(row.getCell(index));
    }

    /**
     * Yacheykadagi ma'lumotni, uning turi qanday bo'lishidan qat'iy nazar (matn, raqam, sana),
     * har doim to'g'ri matn (String) ko'rinishida olib beradigan ishonchli metod.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return new DataFormatter().formatCellValue(cell).trim();
    }
}