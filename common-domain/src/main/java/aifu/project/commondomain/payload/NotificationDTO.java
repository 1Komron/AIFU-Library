package aifu.project.commondomain.payload;

import aifu.project.commondomain.entity.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,
        String name,
        String surname,
        String phone,
        NotificationType notificationType,
        LocalDateTime time) {
}
