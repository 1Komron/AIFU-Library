package aifu.project.libraryweb.service.exel;

import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.History;
import aifu.project.common_domain.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class ExcelBackupExporter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void exportBookExcel(List<BookExcelDTO> bookList, String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Kitoblar");

        CellStyle cellStyle = createCellStyle(workbook);
        CellStyle headerStyle = createHeaderCellStyle(workbook);

        String[] headers = {
                "Muallif", "Kitob nomi", "Kategoriya", "Seriya raqami",
                "Chop etilgan yil", "Nashriyot", "Chop etilgan shahar", "ISBN", "Sahifalar Soni",
                "Til", "udc", "Nusxalar soni", "Nusxalar inventar raqamlari"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (BookExcelDTO book : bookList) {
            Row row = sheet.createRow(rowNum++);
            int col = 0;

            Cell cell0 = row.createCell(col++);
            cell0.setCellValue((rowNum - 1));
            cell0.setCellStyle(cellStyle);

            Cell cell1 = row.createCell(col++);
            cell1.setCellValue(book.author());
            cell1.setCellStyle(cellStyle);

            Cell cell2 = row.createCell(col++);
            cell2.setCellValue(book.title());
            cell2.setCellStyle(cellStyle);

            Cell cell3 = row.createCell(col++);
            cell3.setCellValue(book.category());
            cell3.setCellStyle(cellStyle);

            Cell cell4 = row.createCell(col++);
            cell4.setCellValue(book.series());
            cell4.setCellStyle(cellStyle);

            Cell cell5 = row.createCell(col++);
            cell5.setCellValue(book.publicationYear());
            cell5.setCellStyle(cellStyle);

            Cell cell6 = row.createCell(col++);
            cell6.setCellValue(book.publisher());
            cell6.setCellStyle(cellStyle);

            Cell cell7 = row.createCell(col++);
            cell7.setCellValue(book.publicationCity());
            cell7.setCellStyle(cellStyle);

            Cell cell8 = row.createCell(col++);
            cell8.setCellValue(book.isbn());
            cell8.setCellStyle(cellStyle);

            Cell cell9 = row.createCell(col++);
            cell9.setCellValue(book.pageCount());
            cell9.setCellStyle(cellStyle);

            Cell cell10 = row.createCell(col);
            cell10.setCellValue(book.language());
            cell10.setCellStyle(cellStyle);

            Cell cell11 = row.createCell(col);
            cell11.setCellValue(book.udc());
            cell11.setCellStyle(cellStyle);

            Cell cell12 = row.createCell(col);
            cell12.setCellValue(book.copyCount());
            cell12.setCellStyle(cellStyle);

            Cell cell13 = row.createCell(col);
            cell13.setCellValue(book.inventoryNumbers().toString());
            cell13.setCellStyle(cellStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath + "/" + createFileName("Kitoblar"))) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            log.error("Kitoblar excel backupda yaratishda xatolik. Xatolik: ", e);
            throw new RuntimeException("Kitoblar excel backupda yaratishda xatolik. Xatolik: ", e);
        }

        log.info("Excel Kitoblar backup muvaffaqiyatli amalga oshirildi");
    }

    public static void exportHistoryExcel(List<History> historyList, String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tarix");

        CellStyle cellStyle = createCellStyle(workbook);
        CellStyle headerStyle = createHeaderCellStyle(workbook);

        String[] headers = {
                "№", "Ism", "Familya", "Telefon raqam",
                "Kitob nomi", "Muallif", "Inventar raqam",
                "Berilgan sana", "Qaytib olingan sana",
                "Kim tomonidan berilgan", "Kim tomonida qabul qilingan"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (History history : historyList) {
            Student user = history.getUser();
            BookCopy bookCopy = history.getBook();

            Row row = sheet.createRow(rowNum++);
            int col = 0;

            Cell cell0 = row.createCell(col++);
            cell0.setCellValue((rowNum - 1));
            cell0.setCellStyle(cellStyle);

            Cell cell1 = row.createCell(col++);
            cell1.setCellValue(user.getName());
            cell1.setCellStyle(cellStyle);

            Cell cell2 = row.createCell(col++);
            cell2.setCellValue(user.getSurname());
            cell2.setCellStyle(cellStyle);

            Cell cell3 = row.createCell(col++);
            cell3.setCellValue(user.getPhoneNumber());
            cell3.setCellStyle(cellStyle);

            Cell cell4 = row.createCell(col++);
            cell4.setCellValue(bookCopy.getBook().getTitle());
            cell4.setCellStyle(cellStyle);

            Cell cell5 = row.createCell(col++);
            cell5.setCellValue(bookCopy.getBook().getAuthor());
            cell5.setCellStyle(cellStyle);

            Cell cell6 = row.createCell(col++);
            cell6.setCellValue(bookCopy.getInventoryNumber());
            cell6.setCellStyle(cellStyle);

            Cell cell7 = row.createCell(col++);
            cell7.setCellValue(history.getGivenAt() != null ? history.getGivenAt().format(formatter) : "");
            cell7.setCellStyle(cellStyle);

            Cell cell8 = row.createCell(col++);
            cell8.setCellValue(history.getReturnedAt() != null ? history.getReturnedAt().format(formatter) : "");
            cell8.setCellStyle(cellStyle);

            Cell cell9 = row.createCell(col++);
            cell9.setCellValue(history.getIssuedBy().getName() + " " + history.getIssuedBy().getSurname());
            cell9.setCellStyle(cellStyle);

            Cell cell10 = row.createCell(col);
            cell10.setCellValue(history.getReturnedBy().getName() + " " + history.getReturnedBy().getSurname());
            cell0.setCellStyle(cellStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath + "/" + createFileName("Tarix"))) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            log.error("Tarix excel backupda yaratishda xatolik. Xatolik: ", e);
            throw new RuntimeException("Tarix excel backupda yaratishda xatolik. Xatolik: ", e);
        }

        log.info("Excel Tarix backup muvaffaqiyatli amalga oshirildi");
    }

    @NotNull
    private static CellStyle createHeaderCellStyle(XSSFWorkbook workbook) {
        Font boldFont = createNormalFont(workbook, true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(boldFont);
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(headerStyle);

        return headerStyle;
    }

    @NotNull
    private static CellStyle createCellStyle(XSSFWorkbook workbook) {
        Font normalFont = createNormalFont(workbook, false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(normalFont);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(cellStyle);

        return cellStyle;
    }

    private static Font createNormalFont(Workbook workbook, boolean isBoldFont) {
        if (isBoldFont) {
            Font boldFont = workbook.createFont();
            boldFont.setFontName("Times New Roman");
            boldFont.setFontHeightInPoints((short) 14);
            boldFont.setBold(true);

            return boldFont;
        } else {
            Font normalFont = workbook.createFont();
            normalFont.setFontName("Times New Roman");
            normalFont.setFontHeightInPoints((short) 14);

            return normalFont;
        }
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


    private static String createFileName(String name) {
        return name + " (" + LocalDate.now().format(formatter) + ")" + ".xlsx";
    }

    private ExcelBackupExporter() {
    }
}
