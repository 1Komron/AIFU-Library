package aifu.project.commondomain.mapper;

import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.payload.WebDTO;
import aifu.project.commondomain.payload.BotDTO;

public class UserMapper {

    public static WebDTO toWebDTO(User user) {
        return new WebDTO(
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getEmail(),
                user.getPassword()
        );
    }

    public static BotDTO toBotDTO(User user) {
        return new BotDTO(
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getEmail(),
                user.getFaculty(),
                user.getCourse(),
                user.getGroup(),
                user.getChatId()
        );
    }

    public static User fromWebDTO(WebDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setPhone(dto.phone());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        return user;
    }

    public static User fromBotDTO(BotDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setPhone(dto.phone());
        user.setEmail(dto.email());
        user.setFaculty(dto.faculty());
        user.setCourse(dto.course());
        user.setGroup(dto.group());
        user.setChatId(dto.chatId());
        return user;
    }
}
