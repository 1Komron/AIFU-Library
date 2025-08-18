package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.StudentImportException;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.config.ImporterColumnProperties;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentExcelImportServiceImpl implements StudentExcelImportService {

    private final StudentRepository studentRepository;
    private final ImporterColumnProperties importerColumnProperties;
    private final PassportHasher passportHasher;
    private final TransactionTemplate transactionTemplate;
    private final ImportErrorReportExcelService importErrorReportExcelService;


    private enum Field {
        PASSPORT_CODE, SURNAME, NAME, DEGREE, FACULTY, CARD_NUMBER,
        ADMISSION_TIME, GRADUATION_TIME
    }

    /**
     * {@inheritDoc}
     * Import jarayonini boshqaruvchi asosiy metod.
     */
    @Override
    public ImportStats importStudentsFromExcel(InputStream inputStream) {
        log.info("Talabalarni Exceldan import qilish jarayoni boshlandi.");
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new StudentImportException("Excel fayl bo'sh yoki sarlavha qatori mavjud emas.");
            }
            Map<Field, Integer> fieldColumnMap = createFieldColumnMap(headerRow);
            List<Student> studentsFromExcel = readUsersDynamically(sheet, fieldColumnMap);

            if (studentsFromExcel.isEmpty()) {
                log.warn("Import to'xtatildi: Excel faylda qayta ishlanadigan ma'lumot topilmadi.");
                return new ImportStats(0, Collections.emptyList());
            }
            log.info("Excel fayldan {} ta yozuv o'qildi. Bazaga saqlash boshlanmoqda...", studentsFromExcel.size());
            return processAndSaveIndividually(studentsFromExcel);

        } catch (StudentImportException e) {
            throw e; // O'zimiz bergan xatoliklarni o'zgartirmasdan tashqariga uzatamiz
        } catch (Exception e) {
            log.error("Import jarayonida kutilmagan texnik xatolik yuz berdi", e);
            throw new StudentImportException("Import jarayonida kutilmagan texnik xatolik: " + e.getMessage(), e);
        }
    }
    private ImportStats processAndSaveIndividually(List<Student> allStudents) {
        int successCount = 0;
        List<ImportErrorDTO> failedRecords = new ArrayList<>();
        Set<String> processedPlainPassports = new HashSet<>(); // Excel ichidagi dublikatlarni aniqlash uchun

        for (Student student : allStudents) {
            String plainPassport = student.getPassportCode();
            String errorReason = null;

            if (!processedPlainPassports.add(plainPassport)) {
                errorReason = "Excel faylning o'zida takrorlangan.";
            } else {
                student.setPassportCode(passportHasher.hash(plainPassport));
                try {
                    transactionTemplate.execute(status -> {
                        studentRepository.save(student);
                        return null;
                    });
                    successCount++;
                } catch (DataIntegrityViolationException e) {
                    log.warn("Dublikat yozuv aniqlandi (Pasport: {}). Sabab: {}", plainPassport, e.getMostSpecificCause().getMessage());
                    if (e.getMostSpecificCause().getMessage().contains("student_passport_code")) {
                        errorReason = "Bu pasport kodli aktiv talaba allaqachon mavjud.";
                    } else if (e.getMostSpecificCause().getMessage().contains("student_card_number")) {
                        errorReason = "Bu karta raqamli aktiv talaba allaqachon mavjud.";
                    } else {
                        errorReason = "Noma'lum takrorlanish xatosi.";
                    }
                } catch (Exception e) {
                    log.error("Talabani saqlashda kutilmagan xato (Pasport: {})", plainPassport, e);
                    errorReason = "Kutilmagan tizim xatoligi.";
                }
            }

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

        ImportStats stats = new ImportStats(successCount, failedRecords);
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

    /**
     * Exceldagi ma'lumot qatorlarini o'qiydi va ulardan `Student` obyektlari ro'yxatini yaratadi.
     * Bu metodga sana maydonlarini to'g'ri o'qish va tahlil qilish logikasi qo'shilgan.
     * @param sheet Excel varag'i.
     * @param fieldColumnMap Sarlavha tahlilidan olingan "xarita".
     * @return `Student` obyektlari ro'yxati.
     */
    private List<Student> readUsersDynamically(Sheet sheet, Map<Field, Integer> fieldColumnMap) {
        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Sarlavhadan keyingi qatordan boshlaymiz
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String passportCode = getCellValueAsString(row.getCell(fieldColumnMap.get(Field.PASSPORT_CODE)));
            // Agar majburiy maydon bo'lgan pasport bo'sh bo'lsa, bu qatorni e'tiborsiz qoldiramiz.
            if (passportCode == null || passportCode.isBlank()) continue;

            Student student = new Student();
            student.setPassportCode(passportCode); // Hozircha ochiq matnni saqlaymiz, u keyin xeshlanadi.
            student.setSurname(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.SURNAME))));
            student.setName(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.NAME))));
            student.setDegree(getOptionalCellValue(row, fieldColumnMap, Field.DEGREE));
            student.setFaculty(getOptionalCellValue(row, fieldColumnMap, Field.FACULTY));
            student.setCardNumber(getOptionalCellValue(row, fieldColumnMap, Field.CARD_NUMBER));

            // Yangi sana maydonlarini o'qish va tahlil qilish
            student.setAdmissionTime(parseDateCellValue(getOptionalCell(row, fieldColumnMap, Field.ADMISSION_TIME)));
            student.setGraduationTime(parseDateCellValue(getOptionalCell(row, fieldColumnMap, Field.GRADUATION_TIME)));

            // Yangi talabalar uchun standart qiymatlar
            student.setRole(Role.STUDENT);
            student.setActive(true);
            student.setDeleted(false);
            students.add(student);
        }
        return students;
    }

    /**
     * `application.yml` dan olingan "laqab"lar (aliases) asosida qidiruv uchun qulay xarita yaratadi.
     * @return `{"familiya" -> SURNAME, "surname" -> SURNAME, ...}` kabi xarita.
     */
    private Map<String, Field> buildAliasMap() {
        Map<String, Field> aliasMap = new HashMap<>();
        addAliases(aliasMap, Field.SURNAME, importerColumnProperties.getSurname());
        addAliases(aliasMap, Field.NAME, importerColumnProperties.getName());
        addAliases(aliasMap, Field.PASSPORT_CODE, importerColumnProperties.getPassportCode());
        addAliases(aliasMap, Field.DEGREE, importerColumnProperties.getDegree());
        addAliases(aliasMap, Field.FACULTY, importerColumnProperties.getFaculty());
        addAliases(aliasMap, Field.CARD_NUMBER, importerColumnProperties.getCardNumber());
        // Yangi maydonlarni xaritaga qo'shish
        addAliases(aliasMap, Field.ADMISSION_TIME, importerColumnProperties.getAdmissionTime());
        addAliases(aliasMap, Field.GRADUATION_TIME, importerColumnProperties.getGraduationTime());
        return aliasMap;
    }

    /**
     * Excel sarlavha qatorini tahlil qilib, ustun nomlarini ichki `Field` enumimizga moslashtiradi.
     * @param headerRow Excel faylning birinchi qatori.
     * @return `{SURNAME: 0, NAME: 1, ...}` kabi xarita.
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

        // Majburiy ustunlar mavjudligini tekshirish
        if (!map.containsKey(Field.PASSPORT_CODE) || !map.containsKey(Field.SURNAME) || !map.containsKey(Field.NAME)) {
            throw new StudentImportException("Excel faylda majburiy ustunlar (pasport, ism, familiya) topilmadi.");
        }
        return map;
    }

    /**
     * Excel katakchasidagi sanani xavfsiz o'qib, LocalDate'ga o'giradigan yordamchi metod.
     * Exceldagi sanalar har xil formatda (matn: "dd.MM.yyyy" yoki raqam) bo'lishini hisobga oladi.
     * @param cell Tahlil qilinishi kerak bo'lgan katakcha.
     * @return `LocalDate` obyekti yoki agar o'qish imkoni bo'lmasa `null`.
     */
    private LocalDate parseDateCellValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                // Agar katakcha raqam formatida bo'lsa (Excelning standart sana formati)
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                // Agar katakcha matn formatida bo'lsa (masalan, "15.08.2025")
                String dateStr = cell.getStringCellValue().trim();
                // Har xil formatlarni sinab ko'rish mumkin. Eng keng tarqalganlari:
                try { return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")); } catch (DateTimeParseException ignored) {}
                try { return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (DateTimeParseException ignored) {}
                // Agar boshqa formatlar ham bo'lsa, shu yerga qo'shiladi.
            }
        } catch (Exception e) {
            log.warn("Katakchadagi sanani o'qishda noma'lum xatolik: '{}'. Sana yozilmadi.", getCellValueAsString(cell));
        }
        return null;
    }

    // --- QOLGAN YORDAMCHI METODLAR ---

    private void addAliases(Map<String, Field> map, Field field, String aliases) {
        if (aliases != null) {
            for (String alias : aliases.split(",")) {
                map.put(alias.trim().toLowerCase(), field);
            }
        }
    }

    private Cell getOptionalCell(Row row, Map<Field, Integer> map, Field field) {
        Integer index = map.get(field);
        return (index == null) ? null : row.getCell(index);
    }

    private String getOptionalCellValue(Row row, Map<Field, Integer> map, Field field) {
        return getCellValueAsString(getOptionalCell(row, map, field));
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return new DataFormatter().formatCellValue(cell).trim();
    }
}