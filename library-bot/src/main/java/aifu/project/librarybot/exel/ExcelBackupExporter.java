package aifu.project.librarybot.exel;

import aifu.project.common_domain.entity.History;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class ExcelBackupExporter {

    public static void exportHistoryExcel(List<History> historyList, String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("List 1");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Font normalFont = workbook.createFont();
        normalFont.setFontName("Times New Roman");
        normalFont.setFontHeightInPoints((short) 14);

        Font boldFont = workbook.createFont();
        boldFont.setFontName("Times New Roman");
        boldFont.setFontHeightInPoints((short) 14);
        boldFont.setBold(true);

        CellStyle normalStyle = workbook.createCellStyle();
        normalStyle.setFont(normalFont);
        normalStyle.setWrapText(true);
        normalStyle.setAlignment(HorizontalAlignment.CENTER);
        normalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(normalStyle);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(boldFont);
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(headerStyle);

        String[] headers = {
                "№", "Ism", "Familya", "Telefon raqam",
                "Kitob nomi", "Muallif", "Inventar raqam",
                "Berilgan sana", "Qaytib olingan sana"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (History history : historyList) {
            Row row = sheet.createRow(rowNum++);
            int col = 0;

            Cell cell0 = row.createCell(col++);
            cell0.setCellValue((rowNum - 1));
            cell0.setCellStyle(normalStyle);

            Cell cell1 = row.createCell(col++);
            cell1.setCellValue(history.getUser().getName());
            cell1.setCellStyle(normalStyle);

            Cell cell2 = row.createCell(col++);
            cell2.setCellValue(history.getUser().getSurname());
            cell2.setCellStyle(normalStyle);

            Cell cell3 = row.createCell(col++);
            cell3.setCellValue(history.getUser().getPhone());
            cell3.setCellStyle(normalStyle);

            Cell cell4 = row.createCell(col++);
            cell4.setCellValue(history.getBook().getBook().getTitle());
            cell4.setCellStyle(normalStyle);

            Cell cell5 = row.createCell(col++);
            cell5.setCellValue(history.getBook().getBook().getAuthor());
            cell5.setCellStyle(normalStyle);

            Cell cell6 = row.createCell(col++);
            cell6.setCellValue(history.getBook().getInventoryNumber());
            cell6.setCellStyle(normalStyle);

            Cell cell7 = row.createCell(col++);
            cell7.setCellValue(history.getGivenAt() != null ? history.getGivenAt().format(formatter) : "");
            cell7.setCellStyle(normalStyle);

            Cell cell8 = row.createCell(col);
            cell8.setCellValue(history.getReturnedAt() != null ? history.getReturnedAt().format(formatter) : "");
            cell8.setCellStyle(normalStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath + "/" + createFileName())) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            log.error("Error saving Excel file", e);
            throw new RuntimeException("Error saving Excel file", e);
        }

        log.info("Excel backup successful");
    }

    private static String createFileName() {
        LocalDate now = LocalDate.now();
        int fromYear = now.minusYears(1).getYear();
        int toYear = now.getYear();

        return fromYear + "-" + toYear + ".xlsx";
    }

    private static void setBorder(CellStyle style) {
        short grey = IndexedColors.GREY_50_PERCENT.getIndex();
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(grey);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(grey);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(grey);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(grey);
    }

    private ExcelBackupExporter() {
    }
}
