package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ImportErrorReportExcelService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateStudentImportErrorReport(List<ImportErrorDTO> studentsWithError) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Import Xatoliklari");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] columns = {"Familiyasi", "Ismi", "Darajasi", "Fakulteti", "O'qishga Kirgan Vaqti", "Bitirish Vaqti", "Xatolik Sababi"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowNum = 1;
            for (ImportErrorDTO student : studentsWithError) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getSurname());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getDegree());
                row.createCell(3).setCellValue(student.getFaculty());
                row.createCell(4).setCellValue(student.getAdmissionTime() != null ? student.getAdmissionTime().format(formatter) : "");
                row.createCell(5).setCellValue(student.getGraduationTime() != null ? student.getGraduationTime().format(formatter) : "");
                row.createCell(6).setCellValue(student.getErrorReason());            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}