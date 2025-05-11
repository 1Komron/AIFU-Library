package aifu.project.libraryweb.mq;

import aifu.project.commondomain.entity.Notification;
import aifu.project.commondomain.repository.NotificationRepository;
import aifu.project.libraryweb.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MQListener {
    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    @RabbitListener(queues = "queue.borrow")
    public void handleBorrow(Notification notification) {
        notificationRepository.save(notification);
        notificationSender.send(notification);
    }

    @RabbitListener(queues = "queue.extend")
    public void handleExtend(Notification notification) {
        notificationRepository.save(notification);
        notificationSender.send(notification);
    }

    @RabbitListener(queues = "queue.return")
    public void handleReturn(Notification notification) {
        notificationRepository.save(notification);
        notificationSender.send(notification);
    }

    @RabbitListener(queues = "queue.register")
    public void handleRegister(Notification notification) {
        notificationRepository.save(notification);
        notificationSender.send(notification);
    }
}
