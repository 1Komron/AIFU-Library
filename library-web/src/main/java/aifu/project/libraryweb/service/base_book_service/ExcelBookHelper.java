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

                BookImportDTO dto = new BookImportDTO(
                        formatter.formatCellValue(row.getCell(2)),
                        formatter.formatCellValue(row.getCell(3)),
                        formatter.formatCellValue(row.getCell(4)),
                        formatter.formatCellValue(row.getCell(5)),
                        Integer.parseInt(formatter.formatCellValue(row.getCell(6))),
                        formatter.formatCellValue(row.getCell(7)),
                        formatter.formatCellValue(row.getCell(8)),
                        formatter.formatCellValue(row.getCell(9)),
                        Integer.parseInt(formatter.formatCellValue(row.getCell(10))),
                        formatter.formatCellValue(row.getCell(11)),
                        formatter.formatCellValue(row.getCell(12)),
                        getInventoryNumbers(formatter.formatCellValue(row.getCell(13)))
                );

                books.add(dto);
            }

            workbook.close();
            return books;

        } catch (Exception e) {
            throw new RuntimeException("Xatolik Excel faylni o‘qishda: " + e.getMessage(), e);
        }
    }

    private static List<String> getInventoryNumbers(String string) {
        return Arrays
                .stream(string.trim().split(","))
                .map(String::trim)
                .toList();
    }

    private ExcelBookHelper() {
    }
}

