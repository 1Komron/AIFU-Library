package aifu.project.commondomain.payload;

public record BookingRequestDTO(
        BotUserDTO user,
        Integer bookId,
        String author,
        String title,
        String isbn,
        String inventoryNumber

) {
}
