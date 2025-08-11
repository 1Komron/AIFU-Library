package aifu.project.common_domain.dto.live_dto;

public record BookCopyShortDTO(
        Integer id,
        String author,
        String title,
        String inventoryNumber,
        String shelfLocation,
        Boolean isTaken
) {
}
