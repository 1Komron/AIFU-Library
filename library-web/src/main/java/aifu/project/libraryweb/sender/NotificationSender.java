package aifu.project.libraryweb.sender;

import aifu.project.common_domain.dto.notification_dto.NotificationShortDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSender {
    private final SimpMessagingTemplate messagingTemplate;

    public void send(NotificationShortDTO notification) {
        log.info("Notification web-socketga uzatildi: {}", notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    public void send(Long notificationId) {
        log.info("Notification web-socketdan o'chirib tashlandi: {}", notificationId);
        messagingTemplate.convertAndSend("topic/notifications/delete", notificationId);
    }
}
