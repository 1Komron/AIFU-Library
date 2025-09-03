package aifu.project.common_domain.dto.booking_dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExtendBookingDTO(
        @NotNull Long bookingId,
        @NotNull @Min(value = 1, message = "Uzaytirish kuni 1 dan kichik bolishi mumkin emas") Integer extendDays) {
}
