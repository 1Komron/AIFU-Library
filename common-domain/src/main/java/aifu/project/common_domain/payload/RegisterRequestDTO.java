package aifu.project.common_domain.payload;

import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.entity.enums.RequestType;

public record RegisterRequestDTO(
        StudentDTO user,
        RequestType requestType,
        NotificationType notificationType) {
}
