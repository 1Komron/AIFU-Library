package aifu.project.commondomain.payload;

import jakarta.validation.constraints.NotNull;

public record BorrowBookRequest(
        @NotNull Long chatId,
        @NotNull Integer bookId,
        @NotNull Boolean accept) {
}
