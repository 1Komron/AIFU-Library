package aifu.project.common_domain.dto.pdf_book_dto;

import java.util.List;

public record CategoryWithBooksDTO(
        CategoryPreviewDTO category,
        List<PdfBookShortDTO> books
) {
}
