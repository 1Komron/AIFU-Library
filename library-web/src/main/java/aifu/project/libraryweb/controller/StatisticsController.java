package aifu.project.libraryweb.controller;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.StatisticsService;
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
    public ResponseEntity<ResponseMessage> getTodayBookings(@RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "5") int pageSize) {
        return statisticsService.getBookingToday(pageNumber, pageSize);
    }

    @GetMapping("/bookings/today/overdue")
    public ResponseEntity<ResponseMessage> getTodayBookingsOverdue(@RequestParam(defaultValue = "1") int pageNumber,
                                                                   @RequestParam(defaultValue = "5") int pageSize) {
        return statisticsService.getBookingTodayOverdue(pageNumber, pageSize);
    }

    @GetMapping("/bookings/perDay")
    public ResponseEntity<ResponseMessage> getPerDayBookings(@RequestParam int month,
                                                             @RequestParam int year) {
        return statisticsService.getBookingPerDay(month, year);
    }

    @GetMapping("/bookings/perMonth")
    public ResponseEntity<ResponseMessage> getPerMonthBookings(@RequestParam int year) {
        return statisticsService.getBookingPerMonth(year);
    }

    @GetMapping("/students/count")
    public ResponseEntity<ResponseMessage> getUserCount() {
        return statisticsService.countUsers();
    }

    @GetMapping("/books/count")
    public ResponseEntity<ResponseMessage> getBookCount() {
        return statisticsService.countBooks();
    }

    @GetMapping("/book/copies/count")
    public ResponseEntity<ResponseMessage> getBookCopiesCount() {
        return statisticsService.countBookCopies();
    }

    @GetMapping("/books/top")
    public ResponseEntity<ResponseMessage> getTopBooks(@RequestParam(defaultValue = "5") int limit) {
        return statisticsService.getTopPopularBooks(limit);
    }

    @GetMapping("/students/top")
    public ResponseEntity<ResponseMessage> getTopStudents(@RequestParam(defaultValue = "5") int limit) {
        return statisticsService.getTopStudents(limit);
    }

    @GetMapping("/average/usage")
    public ResponseEntity<ResponseMessage> getAverageUsage() {
        return statisticsService.getAverageUsageDays();
    }

}
