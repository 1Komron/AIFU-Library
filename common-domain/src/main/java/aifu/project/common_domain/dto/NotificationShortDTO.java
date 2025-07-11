package aifu.project.common_domain.dto;

import aifu.project.common_domain.entity.enums.NotificationType;

public record NotificationShortDTO(
        Long id,
        String name,
        String surname,
        NotificationType notificationType,
        String notificationTime,
        boolean isRead
) {
}
