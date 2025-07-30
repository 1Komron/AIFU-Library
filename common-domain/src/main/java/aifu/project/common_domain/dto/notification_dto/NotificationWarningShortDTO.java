package aifu.project.common_domain.dto.notification_dto;

import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.entity.enums.NotificationType;

import java.time.format.DateTimeFormatter;

public record NotificationWarningShortDTO(
        Long id,
        String bookTitle,
        String bookAuthor,
        String bookCode,
        NotificationType notificationType,
        String date,
        boolean isRead
) implements NotificationShortDTO {
    public static NotificationWarningShortDTO toDTO(Notification notification) {
        String formattedDate = notification.getNotificationTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        BookCopy copy = notification.getBookCopy();
        return new NotificationWarningShortDTO(
                notification.getId(),
                copy.getBook().getTitle(),
                copy.getBook().getAuthor(),
                copy.getInventoryNumber(),
                notification.getNotificationType(),
                formattedDate,
                notification.isRead()
        );
    }
}



