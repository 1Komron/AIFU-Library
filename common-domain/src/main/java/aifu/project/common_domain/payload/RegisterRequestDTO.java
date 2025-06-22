package aifu.project.common_domain.payload;

import aifu.project.common_domain.entity.enums.RequestType;

public record RegisterRequestDTO(
        BotUserDTO user,
        RequestType requestType) {
}
