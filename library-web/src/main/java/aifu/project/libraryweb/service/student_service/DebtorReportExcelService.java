package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class DebtorReportExcelService {

    public byte[] genereateDebtorReport(List<DebtorInfoDTO> debtors) throws Exception{
        try(Workbook workbook =new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            Sheet sheet = workbook.createSheet("Qarzdor talabalar");

            // Sarlavha (Header) uslubini yaratish
            Font headerFont =workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Sarlavha qatorini yaratish
            String [] columns = {"ID", "Ism", "Familiya", "Fakultet"};
            Row headerRow = sheet.createRow(0);
            for(int i = 0; i<columns.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
            }

            // Ma'lumotlarni yozish
            int rowNum = 1;
            for(DebtorInfoDTO debtor : debtors){
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(debtor.getId());
                row.createCell(1).setCellValue(debtor.getName());
                row.createCell(2).setCellValue(debtor.getSurname());
                row.createCell(3).setCellValue(debtor.getFaculty());
            }

            // Ustunlar kengligini avtomatik moslashtirish
             for (int i = 0; i < columns.length; i++){
                 sheet.autoSizeColumn(i);
             }
             workbook.write(outputStream);
             return outputStream.toByteArray();
        }
    }

}
