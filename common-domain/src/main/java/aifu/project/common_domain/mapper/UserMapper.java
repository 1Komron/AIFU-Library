package aifu.project.common_domain.mapper;

import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.payload.BotUserDTO;
import aifu.project.common_domain.payload.WebDTO;

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

    public static BotUserDTO toBotDTO(User user) {
        BotUserDTO dto = new BotUserDTO();
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setPhone(user.getPhone());
        dto.setFaculty(user.getFaculty());
        dto.setCourse(user.getCourse());
        dto.setGroup(user.getGroup());
        dto.setChatId(user.getChatId());
        return dto;
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

    public static User fromBotDTO(BotUserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setFaculty(dto.getFaculty());
        user.setCourse(dto.getCourse());
        user.setGroup(dto.getGroup());
        user.setChatId(dto.getChatId());
        return user;
    }

}
