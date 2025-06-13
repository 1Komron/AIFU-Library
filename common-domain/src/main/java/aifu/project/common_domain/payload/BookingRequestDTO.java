package aifu.project.common_domain.payload;

public record BookingRequestDTO(

        BotUserDTO user,
        Integer bookId,
        String author,
        String title,
        String isbn,
        String inventoryNumber


) {

}
