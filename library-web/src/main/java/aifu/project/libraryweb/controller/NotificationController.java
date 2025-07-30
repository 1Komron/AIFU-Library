package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.bot_service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<ResponseMessage> getUnreadNotification(@RequestParam(defaultValue = "1") int pageNumber,
                                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return notificationService.getUnreadNotifications(pageNumber, pageSize);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getNotificationDetails(@PathVariable String id) {
        return notificationService.getDetails(id);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAllNotifications(@RequestParam(defaultValue = "1") int pageNumber,
                                                               @RequestParam(defaultValue = "10") int pageSize) {
        return notificationService.getAllNotifications(pageNumber, pageSize);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteNotification(@PathVariable Long id) {
       return notificationService.deleteNotification(id);
    }
}
