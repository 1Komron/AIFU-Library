package aifu.project.libraryweb.mq;

import aifu.project.common_domain.dto.notification_dto.NotificationExtendShortDTO;
import aifu.project.common_domain.dto.notification_dto.NotificationWarningShortDTO;
import aifu.project.libraryweb.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMQListener {
    private final NotificationSender notificationSender;

    @RabbitListener(queues = "queue.extend")
    public void handleExtend(NotificationExtendShortDTO notification) {
        log.info("Extend notification: {}", notification);
        notificationSender.send(notification);
    }

    @RabbitListener(queues = "queue.warning")
    public void handleWarning(NotificationWarningShortDTO notification) {
        log.info("Warning notification: {}", notification);
        notificationSender.send(notification);
    }

}
