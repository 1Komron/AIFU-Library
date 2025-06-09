package aifu.project.common_domain.payload;

import jakarta.validation.constraints.NotNull;

public record ReturnBookRequest(
        @NotNull Long chatId,
        @NotNull Integer bookId,
        @NotNull Boolean accept) {
}
