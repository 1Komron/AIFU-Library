package aifu.project.librarybot.session;

import aifu.project.common_domain.payload.BotUserDTO;
import aifu.project.librarybot.enums.RegistrationStep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationState {
    Integer lastMessageId;
    BotUserDTO userDTO;
    RegistrationStep step;
}
