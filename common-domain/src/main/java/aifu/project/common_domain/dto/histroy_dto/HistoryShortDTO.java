package aifu.project.common_domain.dto.histroy_dto;

import aifu.project.common_domain.entity.History;

import java.time.LocalDate;

public record HistoryShortDTO(
        Long id,
        String name,
        String surname,
        String bookTitle,
        String inventoryNumber,
        LocalDate givenAt,
        LocalDate dueDate,
        LocalDate returnedAt
) {
    public static HistoryShortDTO toDTO(History history) {
        return new HistoryShortDTO(
                history.getId(),
                history.getUser().getName(),
                history.getUser().getSurname(),
                history.getBook().getBook().getTitle(),
                history.getBook().getInventoryNumber(),
                history.getGivenAt(),
                history.getDueDate(),
                history.getReturnedAt()
        );
    }
}
