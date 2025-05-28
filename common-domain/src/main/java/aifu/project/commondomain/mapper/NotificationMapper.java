package aifu.project.commondomain.mapper;

import aifu.project.commondomain.entity.Notification;
import aifu.project.commondomain.payload.NotificationDTO;

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
