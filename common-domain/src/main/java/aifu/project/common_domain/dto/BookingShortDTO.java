package aifu.project.common_domain.dto;

import aifu.project.common_domain.entity.enums.Status;

import java.time.LocalDate;

public record BookingShortDTO(
        Long id,
        String title,
        String author,
        LocalDate dueDate,
        LocalDate givenAt,
        Status status
) {
}
