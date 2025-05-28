package aifu.project.commondomain.payload;

public record BookingRequestDTO(
        BotUserDTO user,
        String author,
        String title,
        String isbn,
        String inventoryNumber

) {
}
