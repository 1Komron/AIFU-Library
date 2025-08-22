package aifu.project.libraryweb.service.student_service.utel_service;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.StudentImportException;
import aifu.project.libraryweb.config.ImporterColumnProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel fayllardan talabalar haqidagi ma'lumotlarni o'qish uchun mas'ul bo'lgan
 * markazlashtirilgan yordamchi servis. Bu kod takrorlanishining oldini oladi va
 * mas'uliyatlarni aniq ajratadi.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentExcelReaderService {

    private final ImporterColumnProperties importerColumnProperties;

    // Ichki enum, bu klassning ichki "tili"
    private enum Field {
        PASSPORT_CODE, SURNAME, NAME, DEGREE, FACULTY, CARD_NUMBER,
        ADMISSION_TIME, GRADUATION_TIME
    }

    /**
     * Excel fayl oqimidan talabalarning to'liq ma'lumotlarini o'qiydi.
     * @param inputStream Kiruvchi Excel fayl oqimi.
     * @return `Student` obyektlari ro'yxati.
     */
    public List<Student> readFullStudentDataFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) throw new StudentImportException("Excel faylda varaq (sheet) topilmadi.");

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new StudentImportException("Excel fayl bo'sh yoki sarlavha qatori mavjud emas.");

            Map<Field, Integer> fieldColumnMap = createFieldColumnMap(headerRow);

            List<Student> students = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Faqat pasport kodi mavjud bo'lgan qatorlarni o'qiymiz
                String passportCode = getCellValueAsString(row.getCell(fieldColumnMap.get(Field.PASSPORT_CODE)));
                if (passportCode == null || passportCode.isBlank()) continue;

                Student student = new Student();
                student.setPassportCode(passportCode);
                student.setSurname(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.SURNAME))));
                student.setName(getCellValueAsString(row.getCell(fieldColumnMap.get(Field.NAME))));
                student.setDegree(getOptionalCellValue(row, fieldColumnMap, Field.DEGREE));
                student.setFaculty(getOptionalCellValue(row, fieldColumnMap, Field.FACULTY));
                student.setCardNumber(getOptionalCellValue(row, fieldColumnMap, Field.CARD_NUMBER));
                student.setAdmissionTime(parseDateCellValue(getOptionalCell(row, fieldColumnMap, Field.ADMISSION_TIME)));
                student.setGraduationTime(parseDateCellValue(getOptionalCell(row, fieldColumnMap, Field.GRADUATION_TIME)));

                students.add(student);
            }
            return students;
        } catch (IOException | StudentImportException e) {
            log.error("Excel faylni o'qishda xatolik yuz berdi: {}", e.getMessage());
            throw new StudentImportException("Excel faylni o'qishda xatolik: " + e.getMessage(), e);
        }
    }

    // --- BARCHA YORDAMCHI METODLAR ENDI FAQAT SHU YERDA ---

    private Map<Field, Integer> createFieldColumnMap(Row headerRow) {
        Map<Field, Integer> map = new HashMap<>();
        Map<String, Field> aliasMap = buildAliasMap();
        for (Cell cell : headerRow) {
            String headerText = getCellValueAsString(cell);
            if (headerText != null && !headerText.isBlank()) {
                String cleanHeader = headerText.trim().toLowerCase();
                if (aliasMap.containsKey(cleanHeader)) {
                    map.put(aliasMap.get(cleanHeader), cell.getColumnIndex());
                }
            }
        }
        // Deaktivatsiya jarayonida faqat pasport bo'lishi yetarli.
        if (!map.containsKey(Field.PASSPORT_CODE)) {
            throw new StudentImportException("Excel faylda majburiy 'passport' ustuni topilmadi.");
        }
        return map;
    }

    private Map<String, Field> buildAliasMap() {
        Map<String, Field> aliasMap = new HashMap<>();
        addAliases(aliasMap, Field.PASSPORT_CODE, importerColumnProperties.getPassportCode());
        addAliases(aliasMap, Field.SURNAME, importerColumnProperties.getSurname());
        addAliases(aliasMap, Field.NAME, importerColumnProperties.getName());
        addAliases(aliasMap, Field.DEGREE, importerColumnProperties.getDegree());
        addAliases(aliasMap, Field.FACULTY, importerColumnProperties.getFaculty());
        addAliases(aliasMap, Field.CARD_NUMBER, importerColumnProperties.getCardNumber());
        addAliases(aliasMap, Field.ADMISSION_TIME, importerColumnProperties.getAdmissionTime());
        addAliases(aliasMap, Field.GRADUATION_TIME, importerColumnProperties.getGraduationTime());

        return aliasMap;
    }

    private void addAliases(Map<String, Field> map, Field field, String aliases) {
        if (aliases != null) {
            for (String alias : aliases.split(",")) {
                map.put(alias.trim().toLowerCase(), field);
            }
        }
    }

    private LocalDate parseDateCellValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                try { return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")); } catch (DateTimeParseException ignored) {}
                try { return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (DateTimeParseException ignored) {}
            }
        } catch (Exception e) {
            log.warn("Katakchadagi sanani o'qishda xatolik: '{}'. Sana yozilmadi.", getCellValueAsString(cell));
        }
        return null;
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