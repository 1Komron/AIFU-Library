/*package aifu.project.libraryweb.controller;

import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.libraryweb.service.boot_Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteNotification(@RequestBody Long notificationId) {
        return notificationService.deleteNotification(notificationId);
    }

    @GetMapping("/get/unread")
    public ResponseEntity<ResponseMessage> getUnreadNotification(@RequestParam(defaultValue = "1") int pageNumber,
                                                                 @RequestParam(defaultValue = "8") int pageSize) {
        return notificationService.getUnreadNotifications(pageNumber, pageSize);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ResponseMessage> getNotificationDetails(@PathVariable String id) {
        return notificationService.getDetails(id);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ResponseMessage> getAllNotifications(@RequestParam(defaultValue = "1") int pageNumber,
                                                               @RequestParam(defaultValue = "8") int pageSize) {
        return notificationService.getAllNotifications(pageNumber, pageSize);
    }
}
*/