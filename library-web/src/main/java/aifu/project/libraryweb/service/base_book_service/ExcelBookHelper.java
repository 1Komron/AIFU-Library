package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.live_dto.BookImportDTO;
import aifu.project.common_domain.exceptions.BookImportNonValidHeaderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static aifu.project.libraryweb.service.exel.ExcelBackupExporter.createHeaderCellStyle;
import static aifu.project.libraryweb.service.exel.ExcelBackupExporter.createHeaderRow;

@Slf4j
public class ExcelBookHelper {
    private static final String BOOK_HEADER_INDEX = "#";
    private static final String BOOK_HEADER_AUTHOR = "Muallif";
    private static final String BOOK_HEADER_TITLE = "Kitob nomi";
    private static final String BOOK_HEADER_CATEGORY = "Kategoriya";
    private static final String BOOK_HEADER_SERIES = "Seriya raqami";
    private static final String BOOK_HEADER_TITLE_DETAILS = "Tavsif";
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

    public static byte[] templateExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Kitoblar");
            CellStyle headerStyle = createHeaderCellStyle(workbook);
            createHeaderRow(sheet, BOOK_HEADERS, headerStyle);

            int length = BOOK_HEADERS.length;
            for (int i = 0; i < length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                workbook.write(byteArrayOutputStream);
                byte[] excelBytes = byteArrayOutputStream.toByteArray();

                log.info("Excel shabloni muvaffaqiyatli yaratildi, o'lchami: {} bayt", excelBytes.length);

                return excelBytes;
            }
        } catch (IOException e) {
            log.error("Excel shablonini yaratishda xatolik yuz berdi: {}", e.getMessage());
            return new byte[0];
        }

    }


    public static List<BookImportDTO> excelToBooks(MultipartFile file) {
        try {
            List<BookImportDTO> books = new ArrayList<>();
            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            boolean firstRow = true;

            DataFormatter formatter = new DataFormatter();

            while (rows.hasNext()) {
                Row row = rows.next();

                if (firstRow) {
                    Response response = validateHeader(row);
                    if (!response.valid) {
                        throw new BookImportNonValidHeaderException(
                                "Excel fayl sarlavhalari noto'g'ri formatda. Iltimos tekshirib qayta yuklang.",
                                response.errors
                        );
                    }
                    firstRow = false;
                    continue;
                }

                String author = formatter.formatCellValue(row.getCell(1));
                String title = formatter.formatCellValue(row.getCell(2));
                String category = formatter.formatCellValue(row.getCell(3));
                String series = formatter.formatCellValue(row.getCell(4));
                int publicationYear = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));
                String publisher = formatter.formatCellValue(row.getCell(6));
                String publicationCity = formatter.formatCellValue(row.getCell(7));
                String isbn = formatter.formatCellValue(row.getCell(8));
                int pageCount = Integer.parseInt(formatter.formatCellValue(row.getCell(9)));
                String language = formatter.formatCellValue(row.getCell(10));
                String udc = formatter.formatCellValue(row.getCell(11));
                List<String> inventoryNumbers = getInventoryNumbers(formatter.formatCellValue(row.getCell(12)));

                BookImportDTO dto = new BookImportDTO(
                        author,
                        title,
                        category,
                        series,
                        publicationYear,
                        publisher,
                        publicationCity,
                        isbn,
                        pageCount,
                        language,
                        udc,
                        inventoryNumbers
                );

                books.add(dto);
            }

            workbook.close();
            return books;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getInventoryNumbers(String string) {
        return Arrays
                .stream(string.trim().split(","))
                .filter(inv -> !inv.isBlank())
                .map(String::trim)
                .toList();
    }

    private static Response validateHeader(Row headerRow) {
        int length = BOOK_HEADERS.length;
        boolean success = true;
        List<String> headerErrors = new ArrayList<>();

        if (headerRow == null) {
            return new Response(false, null);
        }

        for (int i = 1; i < length; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !BOOK_HEADERS[i].equalsIgnoreCase(cell.getStringCellValue().trim().toLowerCase())) {
                success = false;
                headerErrors.add("Ustun %d sarlavhasi noto'g'ri. Kutilgan sarlavha: %s".formatted(i, BOOK_HEADERS[i]));
            }
        }
        return new Response(success, headerErrors);
    }

    private ExcelBookHelper() {
    }

    private record Response(boolean valid, List<String> errors) {
    }
}

