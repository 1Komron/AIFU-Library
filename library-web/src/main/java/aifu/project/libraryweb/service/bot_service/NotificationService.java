package aifu.project.libraryweb.service.bot_service;

import aifu.project.common_domain.dto.notification_dto.*;
import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.exceptions.NotificationNotFoundException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.StudentDTO;
import aifu.project.libraryweb.repository.NotificationRepository;
import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private static final String NOTIFICATION_TIME = "notificationTime";

    public ResponseEntity<ResponseMessage> deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found by id: " + notificationId));

        notificationRepository.delete(notification);

        return ResponseEntity.ok(new ResponseMessage(true, "Notification deleted successfully", null));
    }

    public ResponseEntity<ResponseMessage> getUnreadNotifications(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.DESC, NOTIFICATION_TIME));
        Page<Notification> page = notificationRepository.findNotificationByIsRead(false, pageable);

        if (page.getTotalElements() == 0)
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ResponseMessage(false, "No unread notifications", null));

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", getShortDTO(page.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "All unread notifications", map));
    }

    public ResponseEntity<ResponseMessage> getAllNotifications(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.DESC, NOTIFICATION_TIME));
        Page<Notification> page = notificationRepository.findAll(pageable);

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", getShortDTO(page.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "All notifications", map));
    }

    public ResponseEntity<ResponseMessage> getDetails(String notificationId) {
        Notification notification;
        try {
            notification = notificationRepository.findNotificationById(Long.parseLong(notificationId))
                    .orElseThrow(() -> new NotificationNotFoundException("Not found by id: " + notificationId));
        } catch (ClassCastException e) {
            throw new NotificationNotFoundException("Not found by id: " + notificationId);
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return ResponseEntity.ok(new ResponseMessage(
                true,
                "Notification detail",
                getNotificationDetails(notification)));
    }

    private List<NotificationShortDTO> getShortDTO(List<Notification> notifications) {
        return notifications.stream()
                .map(notification ->
                        (NotificationShortDTO) switch (notification.getNotificationType()) {
                            case EXTEND -> NotificationExtendShortDTO.toDTO(notification);
                            case WARNING -> NotificationWarningShortDTO.toDTO(notification);
                        })

                .toList();
    }

    private Object getNotificationDetails(Notification notification) {
        return switch (notification.getNotificationType()) {
            case EXTEND -> new NotificationExtendDetailDTO(
                    notification.getId(),
                    StudentDTO.toDTO(notification.getStudent()),
                    BookDTO.toDTO(notification.getBookCopy()));

            case WARNING -> new NotificationWarningDetailDTO(BookDTO.toDTO(notification.getBookCopy()));
        };
    }
}
