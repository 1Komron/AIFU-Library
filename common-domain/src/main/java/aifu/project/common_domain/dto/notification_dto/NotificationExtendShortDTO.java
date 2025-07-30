package aifu.project.common_domain.dto.notification_dto;

import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.NotificationType;

import java.time.format.DateTimeFormatter;

public record NotificationExtendShortDTO(
        Long id,
        String name,
        String surname,
        NotificationType notificationType,
        String date,
        boolean isRead
) implements NotificationShortDTO {
    public static NotificationExtendShortDTO toDTO(Notification notification) {
        String formattedDate = notification.getNotificationTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Student student = notification.getStudent();
        return new NotificationExtendShortDTO(
                notification.getId(),
                student.getName(),
                student.getSurname(),
                notification.getNotificationType(),
                formattedDate,
                notification.isRead()
        );
    }
}
