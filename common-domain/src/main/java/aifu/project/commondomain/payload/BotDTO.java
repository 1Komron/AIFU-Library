package aifu.project.commondomain.payload;

public record BotDTO(
        String name,
        String surname,
        String phone,
        String email,
        String faculty,
        String course,
        String group,
        Long chatId
) {

}
