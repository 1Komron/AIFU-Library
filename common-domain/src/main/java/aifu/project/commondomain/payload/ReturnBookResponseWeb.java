package aifu.project.commondomain.payload;

public record ReturnBookResponseWeb (Long chatId, Integer bookId, Boolean accept, Long notificationId){
}
