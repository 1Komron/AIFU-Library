package aifu.project.librarybot.controller;

import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.librarybot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<ResponseMessage> deleteNotification(@PathVariable Long id) {
//        return notificationService.deleteNotification(id);
//    }

    @GetMapping("/get/unread")
    public ResponseEntity<ResponseMessage> getUnreadNotification(@RequestParam(defaultValue = "1") int pageNumber,
                                                                 @RequestParam(defaultValue = "8") int pageSize) {

        return notificationService.getUnreadNotifications(pageNumber, pageSize);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ResponseMessage> getAllNotifications(@RequestParam(defaultValue = "1") int pageNumber,
                                                               @RequestParam(defaultValue = "8") int pageSize) {
        return notificationService.getAllNotifications(pageNumber, pageSize);
    }
}
