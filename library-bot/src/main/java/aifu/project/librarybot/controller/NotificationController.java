package aifu.project.librarybot.controller;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.librarybot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<ResponseMessage> getUnreadNotification(@RequestParam(defaultValue = "1") int pageNumber,
                                                                 @RequestParam(defaultValue = "8") int pageSize) {

        return notificationService.getUnreadNotifications(pageNumber, pageSize);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAllNotifications(@RequestParam(defaultValue = "1") int pageNumber,
                                                               @RequestParam(defaultValue = "8") int pageSize) {
        return notificationService.getAllNotifications(pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getNotificationDetails(@PathVariable String id) {
        return notificationService.get(id);
    }
}
