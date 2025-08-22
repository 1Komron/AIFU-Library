package aifu.project.libraryweb.service.exel;

import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.entity.*;
import aifu.project.common_domain.entity.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
public class ExcelBackupExporter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String BOOK_HEADER_INDEX = "#";
    private static final String BOOK_HEADER_AUTHOR = "Muallif";
    private static final String BOOK_HEADER_TITLE = "Kitob nomi";
    private static final String BOOK_HEADER_CATEGORY = "Kategoriya";
    private static final String BOOK_HEADER_SERIES = "Seriya raqami";
    private static final String BOOK_HEADER_TITLE_DETAILS = "Kitob nomi (qo'shimcha)";
    private static final String BOOK_HEADER_YEAR = "Chop etilgan yil";
    private static final String BOOK_HEADER_PUBLISHER = "Nashriyot";
    private static final String BOOK_HEADER_CITY = "Chop etilgan shahar";
    private static final String BOOK_HEADER_ISBN = "ISBN";
    private static final String BOOK_HEADER_PAGES = "Sahifalar soni";
    private static final String BOOK_HEADER_LANGUAGE = "Til";
    private static final String BOOK_HEADER_UDC = "UDC";
    private static final String BOOK_HEADER_COPY_COUNT = "Nusxalar soni";
    private static final String BOOK_HEADER_INVENTORY_NUMBERS = "Inventar raqamlari";

    private static final String[] BOOK_HEADERS = {
            BOOK_HEADER_INDEX, BOOK_HEADER_AUTHOR, BOOK_HEADER_TITLE,
            BOOK_HEADER_CATEGORY, BOOK_HEADER_SERIES, BOOK_HEADER_TITLE_DETAILS,
            BOOK_HEADER_YEAR, BOOK_HEADER_PUBLISHER, BOOK_HEADER_CITY,
            BOOK_HEADER_ISBN, BOOK_HEADER_PAGES, BOOK_HEADER_LANGUAGE,
            BOOK_HEADER_UDC, BOOK_HEADER_COPY_COUNT, BOOK_HEADER_INVENTORY_NUMBERS
    };

    private static final String BOOKING_HEADER_INDEX = "#";
    private static final String BOOKING_HEADER_STUDENT = "Student";
    private static final String BOOKING_HEADER_BOOK = "Kitob";
    private static final String BOOKING_HEADER_BOOK_COPY = "Kitob nusxasi";
    private static final String BOOKING_HEADER_STATUS = "Holat";
    private static final String BOOKING_HEADER_START_DATE = "Boshlanish sanasi";
    private static final String BOOKING_HEADER_END_DATE = "Tugash sanasi";

    private static final String[] BOOKING_HEADERS = {
            BOOKING_HEADER_INDEX, BOOKING_HEADER_STUDENT, BOOKING_HEADER_BOOK,
            BOOKING_HEADER_BOOK_COPY, BOOKING_HEADER_STATUS,
            BOOKING_HEADER_START_DATE, BOOKING_HEADER_END_DATE
    };

    private static final String STUDENT_HEADER_INDEX = "№";
    private static final String STUDENT_HEADER_NAME = "Ism";
    private static final String STUDENT_HEADER_SURNAME = "Familya";
    private static final String STUDENT_HEADER_PHONE = "Telefon raqam";
    private static final String STUDENT_HEADER_CARD = "Karta raqami";
    private static final String STUDENT_HEADER_DIRECTION = "Yo'nalish";
    private static final String STUDENT_HEADER_DEGREE = "Daraja";
    private static final String STUDENT_HEADER_START_DATE = "Qabul qilingan sana";
    private static final String STUDENT_HEADER_END_DATE = "Tugatish sanasi";

    private static final String[] STUDENT_HEADERS = {
            STUDENT_HEADER_INDEX, STUDENT_HEADER_NAME, STUDENT_HEADER_SURNAME,
            STUDENT_HEADER_PHONE, STUDENT_HEADER_CARD, STUDENT_HEADER_DIRECTION,
            STUDENT_HEADER_DEGREE, STUDENT_HEADER_START_DATE, STUDENT_HEADER_END_DATE
    };

    private static final String HISTORY_HEADER_INDEX = "#";
    private static final String HISTORY_HEADER_STUDENT_NAME = "Student";
    private static final String HISTORY_HEADER_BOOK_TITLE = "Kitob";
    private static final String HISTORY_HEADER_BORROW_DATE = "Olingan sana";
    private static final String HISTORY_HEADER_RETURN_DATE = "Qaytarilgan sana";
    private static final String HISTORY_HEADER_STATUS = "Holat";

    private static final String[] HISTORY_HEADERS = {
            HISTORY_HEADER_INDEX, HISTORY_HEADER_STUDENT_NAME, HISTORY_HEADER_BOOK_TITLE,
            HISTORY_HEADER_BORROW_DATE, HISTORY_HEADER_RETURN_DATE, HISTORY_HEADER_STATUS
    };


    public static byte[] exportBookExcel(List<BookExcelDTO> bookList) {
        return exportExcel(
                "Kitoblar",
                BOOK_HEADERS,
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
                }
        );
    }

    public static byte[] exportHistoryExcel(List<History> historyList) {
        return exportExcel(
                "Tarix",
                HISTORY_HEADERS,
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
                }
        );
    }

    public static byte[] exportBookingExcel(List<Booking> bookings) {
        return exportExcel(
                "Bronlar ro'yxati",
                BOOKING_HEADERS,
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
                }
        );
    }

    public static byte[] exportStudent(List<Student> students) {
        return exportExcel(
                "Studentlar",
                STUDENT_HEADERS,
                students,
                (row, student) -> {
                    int col = 0;
                    setCell(row, col++, row.getRowNum());
                    setCell(row, col++, student.getName());
                    setCell(row, col++, student.getSurname());
                    setCell(row, col++, student.getPhoneNumber());
                    setCell(row, col++, student.getCardNumber());
                    setCell(row, col++, student.getFaculty());
                    setCell(row, col++, student.getDegree());
                    setCell(row, col++, student.getAdmissionTime());
                    setCell(row, col, student.getGraduationTime());
                }
        );
    }

    private static <T> byte[] exportExcel(
            String sheetName,
            String[] headers,
            List<T> data,
            BiConsumer<Row, T> rowFiller) {
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

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);

                log.info("Excel {} backup muvaffaqiyatli amalga oshirildi", sheetName);

                return out.toByteArray();
            }
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

    private ExcelBackupExporter() {
    }
}
