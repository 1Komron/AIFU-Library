package aifu.project.common_domain.dto.booking_dto;

public record ExtendBookingDTO(Long bookingId, Integer extendDays) {
    public ExtendBookingDTO {
        if (bookingId == null || bookingId <= 0) {
            throw new IllegalArgumentException("Booking ID must be a positive number.");
        }
        if (extendDays == null || extendDays <= 0) {
            throw new IllegalArgumentException("Extend days must be a positive number.");
        }
    }
}
