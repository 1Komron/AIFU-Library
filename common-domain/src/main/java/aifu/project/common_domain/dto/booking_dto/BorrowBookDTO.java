package aifu.project.common_domain.dto.booking_dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BorrowBookDTO(
        @NotBlank String cardNumber,
        @NotNull Integer id,

        @Min(1)
        @NotBlank Integer days) {
}
