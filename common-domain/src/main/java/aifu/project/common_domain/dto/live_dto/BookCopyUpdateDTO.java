package aifu.project.common_domain.dto.live_dto;

public record BookCopyUpdateDTO(
        String inventoryNumber,
        String shelfLocation,
        String notes,
        Integer baseBookId,
        String epc
) {
}
