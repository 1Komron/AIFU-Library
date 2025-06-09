package aifu.project.common_domain.payload;

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
