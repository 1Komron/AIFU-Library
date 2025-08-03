package aifu.project.common_domain.dto.live_dto;

public record BaseBookShortDTO(
        Integer id,
        String title,
        String author,
        BaseBookCategoryDTO category,
        String isbn,
        Long totalCopies,
        Long takenCopies
) {
}
