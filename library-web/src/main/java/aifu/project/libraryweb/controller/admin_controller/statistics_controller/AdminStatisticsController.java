package aifu.project.libraryweb.controller.admin_controller.statistics_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.statistics_service.AdminStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/admin-statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {
    private final AdminStatisticsService adminStatisticsService;

    @GetMapping("/issued-books")
    @Operation(summary = "Berilgan kitoblar sonini olib chiqish.",
            description = """
                    Berilgan kitoblar sonini olib chiqish
                    Parametrlar:
                    -'period': 'current-month' (default) ,last-month
                    """)
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> getIssuedBooks(@RequestParam(defaultValue = "current-month") String period) {
        return adminStatisticsService.getIssuedBooks(period);
    }

    @GetMapping("/extended-books")
    @Operation(summary = "Vaqti uzaytirilgan kitoblar sonini olib chiqish.",
            description = """
                    Vaqti uzaytirilgan kitoblar sonini olib chiqish
                    Parametrlar:
                    -period: 'current-month' (default) ,last-month
                    """)
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> getExtendedBooks(@RequestParam(defaultValue = "current-month") String period) {
        return adminStatisticsService.getExtendedBooks(period);
    }

    @GetMapping("/return-books")
    @Operation(summary = "Qaytib olingan kitoblar sonini olib chiqish.",
            description = """
                    Qaytib olingan kitoblar sonini olib chiqish
                    Parametrlar:
                    -period: 'current-month' (default) ,last-month
                    """)
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> getReturnBooks(@RequestParam(defaultValue = "current-month") String period) {
        return adminStatisticsService.getReturnBooks(period);
    }

    @GetMapping("/activity/today")
    @Operation(summary = "Bugungi bajarilgan amallar ro'yxati va digrammani olib chiqish")
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> getActivityToday() {
        return adminStatisticsService.getActivityToday();
    }

    @GetMapping("/activity/analytics")
    @Operation(summary = "Bajarilgan amallar statistikasini olib chiqish.",
            description = """
                    Bajarilgan amallar statistikasini olib chiqish
                    Parametrlar:
                    -period: 'current-month' (default) ,last-month
                    """)
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> getActivityAnalytics(@RequestParam(defaultValue = "current-month") String period) {
        return adminStatisticsService.getActivityAnalytics(period);
    }

    @GetMapping("/activity")
    @Operation(summary = "Bajarilgan amallar ro'yxati va digrammani olib chiqish")
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> getActivity(@RequestParam(defaultValue = "current-month") String period) {
        return adminStatisticsService.getActivity(period);
    }

    @GetMapping("/top")
    @Operation(summary = "Adminlar bajargan ishlari boyicha ornini korish",
            description = """
                    Bajarilgan amallar statistikasini olib chiqish
                    Parametrlar:
                    -period: 'current-month' (default) ,last-month
                    """)
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> getTop(@RequestParam(defaultValue = "current-month") String period) {
        return adminStatisticsService.getTop(period);
    }
}
