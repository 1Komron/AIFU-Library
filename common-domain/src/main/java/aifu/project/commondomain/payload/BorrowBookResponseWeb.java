package aifu.project.commondomain.payload;

public record BorrowBookResponseWeb(Long chatId, Integer bookId, Boolean accept, Long notificationId) {
}
