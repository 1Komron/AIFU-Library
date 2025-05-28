package aifu.project.libraryweb.sender;

import aifu.project.commondomain.entity.Notification;
import aifu.project.commondomain.payload.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationSender {

    private final SimpMessagingTemplate messagingTemplate;

    public void send(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    public void send(Long notificationId) {
        messagingTemplate.convertAndSend("topic/notifications/delete", notificationId);
    }


}
