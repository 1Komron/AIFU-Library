package aifu.project.commondomain.payload;

import java.time.LocalDateTime;

public record RegisterRequestDTO(
        BotUserDTO user,
        LocalDateTime date
) {
}
