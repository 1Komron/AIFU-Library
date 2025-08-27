package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BookImportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ExcelBookHelper {

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

        } catch (Exception e) {
            throw new IllegalArgumentException("Xatolik Excel faylni o‘qishda: " + e.getMessage(), e);
        }
    }

    private static List<String> getInventoryNumbers(String string) {
        return Arrays
                .stream(string.trim().split(","))
                .filter(inv -> !inv.isBlank())
                .map(String::trim)
                .toList();
    }

    private ExcelBookHelper() {
    }
}

