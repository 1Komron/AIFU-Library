package aifu.project.common_domain.dto.excel_dto;

import java.util.List;

public record BookExcelDTO(
        String author,
        String title,
        String category,
        String series,
        Integer publicationYear,
        String publisher,
        String publicationCity,
        String isbn,
        Integer pageCount,
        String language,
        String udc,
        Long copyCount,
        List<String> inventoryNumbers
) {
}
