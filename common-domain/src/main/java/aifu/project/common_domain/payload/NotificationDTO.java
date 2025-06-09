package aifu.project.common_domain.payload;

import aifu.project.common_domain.entity.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,
        String name,
        String surname,
        String phone,
        NotificationType notificationType,
        LocalDateTime time) {
}
