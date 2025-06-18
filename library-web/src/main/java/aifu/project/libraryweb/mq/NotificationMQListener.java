package aifu.project.libraryweb.mq;

import aifu.project.common_domain.payload.NotificationDTO;
import aifu.project.libraryweb.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMQListener {
    private final NotificationSender notificationSender;
/*
    @RabbitListener(queues = "queue.borrow")
    public void handleBorrow(NotificationDTO notification) {
        notificationSender.send(notification);
    }

    @RabbitListener(queues = "queue.extend")
    public void handleExtend(NotificationDTO notification) {
        notificationSender.send(notification);
    }

    @RabbitListener(queues = "queue.return")
    public void handleReturn(NotificationDTO notification) {
        notificationSender.send(notification);
    }

    @RabbitListener(queues = "queue.register")
    public void handleRegister(NotificationDTO notification) {
        notificationSender.send(notification);
        }
        */
}
