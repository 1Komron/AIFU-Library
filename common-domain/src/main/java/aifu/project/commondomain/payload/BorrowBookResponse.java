package aifu.project.commondomain.payload;

public record BorrowBookResponse(Long chatId, Integer bookId, Boolean accept) {
}
