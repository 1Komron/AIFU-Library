package aifu.project.common_domain.dto;

import aifu.project.common_domain.entity.enums.Status;

import java.time.LocalDate;

public record BookingSummaryDTO(
        Long id,
        String title,
        String author,
        String inventoryNumber,
        LocalDate dueDate,
        LocalDate givenAt,
        Status status,
        Long userId,
        String name,
        String surname
) {
}
