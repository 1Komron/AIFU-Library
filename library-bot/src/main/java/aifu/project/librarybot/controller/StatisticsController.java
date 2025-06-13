package aifu.project.librarybot.controller;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.librarybot.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/bookings/count")
    public ResponseEntity<ResponseMessage> getTotalBookings() {
        return statisticsService.countAllBookings();
    }

    @GetMapping("/bookings/diagram")
    public ResponseEntity<ResponseMessage> getBookingDiagram() {
        return statisticsService.getBookingDiagram();
    }

    @GetMapping("/bookings/today")
    public ResponseEntity<ResponseMessage> getTodayBookings(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return statisticsService.getBookingToday(pageNumber, pageSize);
    }

    @GetMapping("/bookings/today/overdue")
    public ResponseEntity<ResponseMessage> getTodayBookingsOverdue(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return statisticsService.getBookingTodayOverdue(pageNumber, pageSize);
    }

    @GetMapping("/bookings/perMonth")
    public ResponseEntity<ResponseMessage> getPerMonthBookings(@RequestParam int month) {
        return statisticsService.getBookingPerMonth(month);
    }
}
