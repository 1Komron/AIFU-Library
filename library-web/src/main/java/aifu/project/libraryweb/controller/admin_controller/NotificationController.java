package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.notification_service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{id}")
    @Operation(summary = "Notification ID boyicha ma'lumotlarni olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification ma'lumotlari muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "404", description = "Notification topilmadi")
    })
    public ResponseEntity<ResponseMessage> getNotificationDetails(@PathVariable String id) {
        return notificationService.getDetails(id);
    }

    @GetMapping
    @Operation(summary = "Notification ro'yxatini olish",
            description = """
                    Parametrlar:
                    - pageNumber: Sahifa raqami (default: 1)
                    - pageSize: Sahifa hajmi (default: 10)
                    - filter: Filtrlash turi (default: 'all') 'all', 'unread', 'read'
                    - sortDirection: Tartiblash yo'nalishi (default: asc) 'asc' yoki 'desc'
                    """)
    public ResponseEntity<ResponseMessage> getAllNotifications(@RequestParam(defaultValue = "1") int pageNumber,
                                                               @RequestParam(defaultValue = "10") int pageSize,
                                                               @RequestParam(required = false, defaultValue = "all") String filter,
                                                               @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return notificationService.getAllNotifications(pageNumber, pageSize, filter, sortDirection);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Notification o'chirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification muvaffaqiyatli o'chirildi"),
            @ApiResponse(responseCode = "404", description = "Notification topilmadi")
    })
    public ResponseEntity<ResponseMessage> deleteNotification(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }
}
