package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.Notification;
import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.enums.NotificationType;
import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.commondomain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification createNotification(User user, NotificationType type, Long requestId) {
        Notification notification = new Notification();
        notification.setRequestId(requestId);
        notification.setUserName(user.getName());
        notification.setUserSurname(user.getSurname());
        notification.setPhone(user.getPhone());
        notification.setNotificationType(type);
        notification.setNotificationTime(LocalDateTime.now());
        return notification;
    }

    public ResponseEntity<ResponseMessage> deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.getNotificationById(notificationId);

        if (notification == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage(false, "Notification not found by id" + notificationId, null));

        notificationRepository.delete(notification);

        return ResponseEntity.ok(new ResponseMessage(true, "Notification deleted", notification));
    }

    public ResponseEntity<ResponseMessage> getUnreadNotifications(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "notificationTime"));
        Page<Notification> page = notificationRepository.findNotificationByIsRead(false, pageable);
        if (page.getTotalElements() == 0)
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ResponseMessage(false, "No unread notifications", null));

        Map<String, Object> pageInfo = Map.of(
                "pageNumber", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "data", page.get()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "All unread notifications", pageInfo));
    }

    public ResponseEntity<ResponseMessage> getAllNotifications(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "notificationTime"));
        Page<Notification> page = notificationRepository.findAll(pageable);
        if (page.getTotalElements() == 0)
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ResponseMessage(false, "Empty", null));

        Map<String, Object> pageInfo = Map.of(
                "pageNumber", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "data", page.get()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "All notifications", pageInfo));
    }
}
