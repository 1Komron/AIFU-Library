package aifu.project.common_domain.dto.booking_dto;

import jakarta.validation.constraints.NotNull;

public record ReturnBookDTO(@NotNull Long bookingId) {
}
