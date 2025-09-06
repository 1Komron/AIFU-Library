package aifu.project.common_domain.dto.booking_dto;

import java.time.LocalDate;

public record BookingResponse(
        String studentFullName,
        String adminFullName,
        String author,
        String title,
        LocalDate dueDate) {
}
