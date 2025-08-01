package aifu.project.libraryweb.service.notification_service;

import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<ResponseMessage> deleteNotification(Long notificationId);

    ResponseEntity<ResponseMessage> getAllNotifications(int pageNumber, int pageSize, String filter, String sortDirection);

    ResponseEntity<ResponseMessage> getDetails(String notificationId);
}
