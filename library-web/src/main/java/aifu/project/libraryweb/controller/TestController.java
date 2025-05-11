package aifu.project.libraryweb.controller;

import aifu.project.commondomain.entity.Notification;
import aifu.project.libraryweb.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final NotificationSender notificationSender;

    @GetMapping("/test-notify")
    public void testNotify() {
        Notification notification = new Notification();
        notification.setUserName("Test");
        notification.setUserSurname("Пользователь хочет взять книгу.");
        notificationSender.send(notification);
    }
}

