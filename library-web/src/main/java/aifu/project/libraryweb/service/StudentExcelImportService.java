package aifu.project.libraryweb.service;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.config.ImporterColumnProperties;
import aifu.project.libraryweb.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
@RequiredArgsConstructor
@Service
public class StudentExcelImportService {
    private final StudentRepository studentRepository;
    private final ImporterColumnProperties importerColumnProperties;

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
        // try-with-resources bloki workbook'ni ish oxirida avtomatik yopilishini ta'minlaydi.
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                // Agar fayl bo'sh bo'lsa yoki sarlavha qatori bo'lmasa, tushunarli xatolik "otamiz".
                throw new IllegalArgumentException("Excel fayl bo'sh yoki sarlavha qatori mavjud emas.");
            }

            // 1-QADAM: Dinamik "ustunlar xaritasi"ni yaratish.
            // Bu xarita bizga qaysi ma'lumot (masalan, SURNAME) qaysi ustunda (masalan, 1-indeks) ekanligini aytadi.
            Map<Field, Integer> fieldColumnMap = createFieldColumnMap(headerRow);

            // 2-QADAM: Yuqoridagi xarita yordamida Exceldagi barcha ma'lumotlarni o'qib, User obyektlari ro'yxatiga aylantirish.
            List<Student> studentsFromExcel = readUsersDynamically(sheet, fieldColumnMap);

            if (studentsFromExcel.isEmpty()) {
                // Agar o'qishga arziydigan ma'lumot topilmasa, bo'sh hisobot qaytaramiz.
                return new ImportStats(0, List.of("Excel faylda import qilinadigan yangi ma'lumot topilmadi."));
            }

            // 3-QADAM: O'qilgan ma'lumotlarni baza bilan solishtirish va faqat yangilarini saqlash.
            return filterAndSaveUsers(studentsFromExcel);

        } catch (Exception e) {
            // Jarayondagi har qanday xatolikni ushlab, Controller'ga yetkazamiz.
            throw new RuntimeException("Import jarayonida kutilmagan xatolik: " + e.getMessage(), e);
        }
    }

    /**
     * Exceldan olingan ro'yxatni bazadagi mavjud ma'lumotlar bilan solishtiradi va yakuniy hisobotni shakllantiradi.
     */
    private ImportStats filterAndSaveUsers(List<Student> usersFromExcel) {
        List<Student> newStudentToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> processedPassportCodesInExcel = new HashSet<>();
        // Bitta so'rov bilan bazadagi BARCHA mavjud pasport kodlarini olamiz. Bu eng samarali usul.
        Set<String> existingDbPassportCodes = studentRepository.findAllPassportCodes();

        for (Student student : usersFromExcel) {
            String passportCode = student.getPassportCode();

            if (existingDbPassportCodes.contains(passportCode)) {
                errors.add("Talaba (Pasport: " + passportCode + ") ma'lumotlar bazasida allaqachon mavjud.");
                continue;
            }
            if (processedPassportCodesInExcel.contains(passportCode)) {
                errors.add("Talaba (Pasport: " + passportCode + ") Excel faylida takroran kelgan. E'tiborga olinmadi.");
                continue;
            }
            newStudentToSave.add(student);
            processedPassportCodesInExcel.add(passportCode);
        }

        if (!newStudentToSave.isEmpty()) {
            // Bitta katta so'rov bilan barchasini bazaga yozamiz.
            studentRepository.saveAll(newStudentToSave);
        }

        // Natija va xatoliklar haqida hisobotni (ImportStats) qaytaramiz.
        return new ImportStats(newStudentToSave.size(), errors);
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
     * Exceldagi ma'lumot qatorlarini o'qiydi. U endi qotirilgan indekslarga emas,
     * balki dinamik yaratilgan "ustunlar xaritasi"ga tayanib ishlaydi.
     */
    private List<Student> readUsersDynamically(Sheet sheet, Map<Field, Integer> fieldColumnMap) {
        List<Student> students = new ArrayList<>();
        int headerRowNum = 0;

        for (int i = headerRowNum + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) break;

            // Pasport kodini o'qiymiz. Uning indeksi endi xaritadan olinadi. Bu biz uchun asosiy signal.
            String passportCode = getCellValueAsString(row.getCell(fieldColumnMap.get(Field.PASSPORT_CODE)));
            // Agar pasport kodi bo'sh bo'lsa, bu ma'lumotlar tugaganini bildiradi, siklni to'xtatamiz.
            if (passportCode == null || passportCode.isBlank()) break;

            Student student = new Student();
            // Har bir maydonni to'g'ridan-to'g'ri, xaritadan indeksini olib o'rnatamiz.
            student.setPassportCode(passportCode);
            student.setSurname(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.SURNAME))));
            student.setName(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.NAME))));

            // Ixtiyoriy (majburiy bo'lmagan) maydonlarni maxsus metod orqali o'qiymiz.
            student.setDegree(getOptionalCellValue(row, fieldColumnMap, Field.DEGREE));
            student.setFaculty(getOptionalCellValue(row, fieldColumnMap, Field.FACULTY));
            student.setCardNumber(getOptionalCellValue(row, fieldColumnMap, Field.CARD_NUMBER));

            // Har bir yangi foydalanuvchi uchun standart qiymatlarni beramiz.
            student.setRole(Role.STUDENT);
            student.setActive(true);
            student.setDeleted(false);
            students.add(student);
        }
        return students;
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