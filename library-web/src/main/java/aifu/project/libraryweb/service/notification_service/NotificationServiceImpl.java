package aifu.project.libraryweb.service.notification_service;

import aifu.project.common_domain.dto.notification_dto.*;
import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.exceptions.NotificationNotFoundException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.StudentDTO;
import aifu.project.libraryweb.repository.NotificationRepository;
import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    private static final String NOTIFICATION_TIME = "notificationTime";

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> deleteNotification(Long notificationId) {
        log.info("ID: {} orqali notification o'chirish jarayoni...", notificationId);

        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification topilmadi ID: " + notificationId));

        notificationRepository.delete(notification);

        log.info("Notification muvaffaqiyatli ochirildi. NotificationID: {}", notificationId);
        log.info("ID: {} orqali notification ochirish yakunlandi.", notificationId);

        return ResponseEntity.ok(new ResponseMessage(true, "Notification muvaffaqiyatli ochirildi", null));
    }


    @Override
    public ResponseEntity<ResponseMessage> getAllNotifications(int pageNumber, int pageSize, String filter, String sortDirection) {
        log.info("Notification ro'yxatini olish jarayoni...");

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(direction, NOTIFICATION_TIME));

        Page<Notification> page = switch (filter.toLowerCase()) {
            case "unread" -> notificationRepository.findNotificationByIsRead(false, pageable);
            case "read" -> notificationRepository.findNotificationByIsRead(true, pageable);
            default -> notificationRepository.findAll(pageable);
        };

        List<Notification> content = page.getContent();

        log.info("Notification ro'yxati: Ro'yxatdagi elementlar soni: {}, Sahifa raqami: {}, Sahifa hajmi: {}, Filtr: {}, Tartiblash yo'nalishi: {}",
                page.getTotalElements(), pageNumber + 1, pageSize, filter, sortDirection);
        log.info("Notification ro'yxati: Ro'yxat {}", content.stream().map(Notification::getId).toList());

        Map<String, Object> map = Util.getPageInfo(page);
        map.put("data", getShortDTO(content));

        log.info("Notification ro'yxatini olish jaarayoni yakunlandi.");

        return ResponseEntity.ok(new ResponseMessage(true, "Notification ro'yxati", map));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> getDetails(Long notificationId) {
        log.info("ID: {} orqali notification malumotlarini olish jarayoni...", notificationId);

        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification topilmadi. ID: " + notificationId));

        log.info("Notification ma'lumotlari: {}", notification);

        notification.setRead(true);
        notificationRepository.save(notification);

        log.info("Notification o'qilgan deb belgilandi. NotificationID: {}", notificationId);
        log.info("ID: {} oqali notification malumotlarini olish yakunlandi.", notificationId);

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
