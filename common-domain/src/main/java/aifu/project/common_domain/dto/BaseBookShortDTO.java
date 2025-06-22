package aifu.project.common_domain.dto;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;

public record BaseBookShortDTO(
        Integer id,
        String title,
        String author,
        BaseBookCategoryDTO category,
        String isbn,
        Integer totalCopies,
        Integer takenCopies
) {
}
