package aifu.project.libraryweb.controller.admin_controller.statistics_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.statistics_service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/bookings/count")
    @Operation(summary = "Barcha bookinglar soni")
    @ApiResponse(responseCode = "200", description = "Barcha bookinglar soni muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getTotalBookings() {
        return statisticsService.countAllBookings();
    }

    @GetMapping("bookings/overdue/count")
    @Operation(summary = "Barcha vaqti o'tgan bronlar soni")
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getOverdueBookingsCount() {
        return statisticsService.countOverdueBookings();
    }

    @GetMapping("/bookings/diagram")
    @Operation(summary = "Barcha bookinglarning diagramma korinishi")
    @ApiResponse(responseCode = "200", description = "Diagramma qiymatlar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getBookingDiagram() {
        return statisticsService.getBookingDiagram();
    }


    @GetMapping("/bookings/today")
    @Operation(summary = "Bugungi bookinglar")
    @ApiResponse(responseCode = "200", description = "Bugungi bookinglar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getTodayBookings() {
        return statisticsService.getBookingToday();
    }

    @GetMapping("/bookings/today/overdue")
    @Operation(summary = "Bugun vaqti otib ketgan bookinglar")
    @ApiResponse(responseCode = "200", description = "Bugun vaqti otib ketgan bookinglar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getTodayBookingsOverdue() {
        return statisticsService.getBookingTodayOverdue();
    }

    @GetMapping("/bookings/overdue")
    @Operation(summary = "Vaqti otib ketgan barcha bookinglar")
    @ApiResponse(responseCode = "200", description = "Vaqti otib ketgan bookinglar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getBookingsOverdue() {
        return statisticsService.getBookingOverdue();
    }


    @GetMapping("/bookings/perDay")
    @Operation(summary = "Oy davomida har bir kun uchun bookinglar soni")
    @ApiResponse(responseCode = "200", description = "Har bir kun uchun bookinglar soni muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getPerDayBookings(@RequestParam int month,
                                                             @RequestParam int year) {
        return statisticsService.getBookingPerDay(month, year);
    }

    @GetMapping("/bookings/perMonth")
    @Operation(summary = "Yil davomida har bir oy uchun bookinglar soni")
    @ApiResponse(responseCode = "200", description = "Har bir oy uchun bookinglar soni muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getPerMonthBookings(@RequestParam int year) {
        return statisticsService.getBookingPerMonth(year);
    }

    @GetMapping("/students/count")
    @Operation(summary = "Barcha studentlar soni")
    @ApiResponse(responseCode = "200", description = "Barcha studentlar soni muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getUserCount() {
        return statisticsService.countUsers();
    }

    @GetMapping("/books/count")
    @Operation(summary = "Barcha base book lar soni")
    @ApiResponse(responseCode = "200", description = "Barcha base book lar soni muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getBookCount() {
        return statisticsService.countBooks();
    }

    @GetMapping("/book/copies/count")
    @Operation(summary = "Barcha book copy lar soni")
    @ApiResponse(responseCode = "200", description = "Barcha book copy lar soni muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getBookCopiesCount() {
        return statisticsService.countBookCopies();
    }

    @GetMapping("/books/top")
    @Operation(summary = "Eng mashhur kitoblar | Top 5")
    @ApiResponse(responseCode = "200", description = "Eng mashhur kitoblar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getTopBooks() {
        return statisticsService.getTopPopularBooks();
    }

    @GetMapping("/students/top")
    @Operation(summary = "Eng faol studentlar | Top 5")
    @ApiResponse(responseCode = "200", description = "Eng faol studentlar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getTopStudents() {
        return statisticsService.getTopStudents();
    }

    @GetMapping("/average/usage")
    @Operation(summary = "O'rtacha foydalanish kunlari")
    @ApiResponse(responseCode = "200", description = "O'rtacha foydalanish kunlari muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getAverageUsage() {
        return statisticsService.getAverageUsageDays();
    }

}
