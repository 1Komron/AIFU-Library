package aifu.project.common_domain.dto.live_dto;

public record BaseBookResponseDTO(
        Integer id,
        String author,
        String title,
        String series,
        String titleDetails,
        Integer publicationYear,
        String publisher,
        String publicationCity,
        String isbn,
        Integer pageCount,
        String language,
        String udc,
        BaseBookCategoryDTO category) {
}
