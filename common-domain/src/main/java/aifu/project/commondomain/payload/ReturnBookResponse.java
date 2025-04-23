package aifu.project.commondomain.payload;

public record ReturnBookResponse(Long chatId, Integer bookId, Boolean success) {
}
