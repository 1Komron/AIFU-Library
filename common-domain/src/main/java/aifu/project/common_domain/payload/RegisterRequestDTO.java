package aifu.project.common_domain.payload;

import java.time.LocalDateTime;

public record RegisterRequestDTO(
        BotUserDTO user,
        LocalDateTime date
) {
}
