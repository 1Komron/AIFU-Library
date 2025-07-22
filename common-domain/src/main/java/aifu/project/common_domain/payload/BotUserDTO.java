package aifu.project.common_domain.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BotUserDTO {
    String name;
    String surname;
    String email;
    String faculty;
    String course;
    String group;
    Long chatId;
}
