package aifu.project.common_domain.dto.booking_dto;

public record BorrowBookDTO(String cardNumber, Integer id, Integer days) {
    public BorrowBookDTO {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number cannot be null or blank");
        }
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
        if (days == null || days <= 0) {
            throw new IllegalArgumentException("Days must be a positive integer");
        }
    }
}
