package aifu.project.common_domain.mapper;

import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.payload.NotificationDTO;

public class NotificationMapper {
    public static NotificationDTO notificationToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getUserName(),
                notification.getUserSurname(),
                notification.getPhone(),
                notification.getNotificationType(),
                notification.getNotificationTime());
    }

    private NotificationMapper() {
    }
}
