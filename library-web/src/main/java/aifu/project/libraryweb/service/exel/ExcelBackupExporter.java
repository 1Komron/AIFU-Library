package aifu.project.libraryweb.service.exel;

import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.entity.*;
import aifu.project.common_domain.entity.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
public class ExcelBackupExporter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void exportBookExcel(List<BookExcelDTO> bookList, String filePath) {
        String[] headers = {
                "#", "Muallif", "Kitob nomi", "Kategoriya", "Seriya raqami",
                "Chop etilgan yil", "Nashriyot", "Chop etilgan shahar", "ISBN",
                "Sahifalar Soni", "Til", "udc", "Nusxalar soni", "Inventar raqamlari"
        };

        exportExcel(
                "Kitoblar",
                headers,
                bookList,
                (row, book) -> {
                    int col = 0;
                    setCell(row, col++, row.getRowNum());
                    setCell(row, col++, book.author());
                    setCell(row, col++, book.title());
                    setCell(row, col++, book.category());
                    setCell(row, col++, book.series());
                    setCell(row, col++, book.publicationYear());
                    setCell(row, col++, book.publisher());
                    setCell(row, col++, book.publicationCity());
                    setCell(row, col++, book.isbn());
                    setCell(row, col++, book.pageCount());
                    setCell(row, col++, book.language());
                    setCell(row, col++, book.udc());
                    setCell(row, col++, book.copyCount());
                    setCell(row, col, String.valueOf(book.inventoryNumbers()));
                },
                filePath
        );
    }

    public static void exportHistoryExcel(List<History> historyList, String filePath) {
        String[] headers = {
                "№", "Ism", "Familya", "Telefon raqam",
                "Kitob nomi", "Muallif", "Inventar raqam",
                "Berilgan sana", "Qaytib olingan sana",
                "Kim tomonidan berilgan", "Kim tomonida qabul qilingan"
        };

        exportExcel(
                "Tarix",
                headers,
                historyList,
                (row, history) -> {
                    Student user = history.getUser();
                    BookCopy bookCopy = history.getBook();
                    int col = 0;
                    setCell(row, col++, row.getRowNum());
                    setCell(row, col++, user.getName());
                    setCell(row, col++, user.getSurname());
                    setCell(row, col++, user.getPhoneNumber());
                    setCell(row, col++, bookCopy.getBook().getTitle());
                    setCell(row, col++, bookCopy.getBook().getAuthor());
                    setCell(row, col++, bookCopy.getInventoryNumber());
                    setCell(row, col++, history.getGivenAt() != null ? history.getGivenAt().format(formatter) : "");
                    setCell(row, col++, history.getReturnedAt() != null ? history.getReturnedAt().format(formatter) : "");
                    setCell(row, col++, history.getIssuedBy().getName() + " " + history.getIssuedBy().getSurname());
                    setCell(row, col, history.getReturnedBy().getName() + " " + history.getReturnedBy().getSurname());
                },
                filePath
        );
    }

    public static void exportBookingExcel(List<Booking> bookings, String filePath, boolean isByStudent) {
        String[] headers = {
                "№", "Ism", "Familya", "Telefon raqam",
                "Kitob nomi", "Muallif", "Inventar raqam",
                "Berilgan sana", "Kim tomonidan berilgan",
                "Tugash vaqti uzaytirilgan kun", "Kim tomonida uzaytirilgan",
                "Holati", "Tugash vaqti"
        };

        String fileName = "Bronlar ro'yxati";
        if (isByStudent && !bookings.isEmpty()) {
            Student user = bookings.get(0).getStudent();
            fileName += " (" + user.getName() + " " + user.getSurname() + ")";
        }

        exportExcel(
                fileName,
                headers,
                bookings,
                (row, booking) -> {
                    Student user = booking.getStudent();
                    BookCopy bookCopy = booking.getBook();
                    int col = 0;
                    setCell(row, col++, row.getRowNum());
                    setCell(row, col++, user.getName());
                    setCell(row, col++, user.getSurname());
                    setCell(row, col++, user.getPhoneNumber());
                    setCell(row, col++, bookCopy.getBook().getTitle());
                    setCell(row, col++, bookCopy.getBook().getAuthor());
                    setCell(row, col++, bookCopy.getInventoryNumber());
                    setCell(row, col++, booking.getGivenAt() != null ? booking.getGivenAt().format(formatter) : "");
                    setCell(row, col++, booking.getIssuedBy().getName() + " " + booking.getIssuedBy().getSurname());
                    setCell(row, col++, booking.getExtendedAt() != null ? booking.getExtendedAt().format(formatter) : "");
                    Librarian ext = booking.getExtendedBy();
                    setCell(row, col++, ext == null ? "" : ext.getName() + " " + ext.getSurname());
                    setCell(row, col++, booking.getStatus() == Status.APPROVED ? "AKTIV" : "VAQTI O'TGAN");
                    setCell(row, col, booking.getDueDate() != null ? booking.getDueDate().format(formatter) : "");
                },
                filePath
        );
    }

    private static <T> void exportExcel(
            String sheetName,
            String[] headers,
            List<T> data,
            BiConsumer<Row, T> rowFiller,
            String filePath
    ) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = createHeaderCellStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);

            createHeaderRow(sheet, headers, headerStyle);

            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                rowFiller.accept(row, item);

                for (Cell cell : row) {
                    cell.setCellStyle(cellStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath + "/" + createFileName(sheetName))) {
                workbook.write(fileOut);
            }

            log.info("Excel {} backup muvaffaqiyatli amalga oshirildi", sheetName);
        } catch (IOException e) {
            log.error("{} excel yaratishda xatolik", sheetName, e);
            throw new RuntimeException(sheetName + " excel yaratishda xatolik", e);
        }
    }

    private static void createHeaderRow(Sheet sheet, String[] headers, CellStyle style) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private static void setCell(Row row, int col, Object value) {
        Cell cell = row.createCell(col);
        if (value instanceof Number num) {
            cell.setCellValue(num.doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
    }

    @NotNull
    private static CellStyle createHeaderCellStyle(XSSFWorkbook workbook) {
        Font boldFont = createFont(workbook, true);
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
        Font font = createFont(workbook, false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(cellStyle);
        return cellStyle;
    }

    private static Font createFont(Workbook workbook, boolean bold) {
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14);
        font.setBold(bold);
        return font;
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
        return name + " (" + LocalDate.now().format(formatter) + ").xlsx";
    }

    private ExcelBackupExporter() {
    }
}
