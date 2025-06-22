package aifu.project.librarybot.controller;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.librarybot.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/booking")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<ResponseMessage> getBookings(@RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        return bookingService.getBookingList(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getBookingById(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseMessage> getBookingByStatus(@RequestParam String status,
                                                              @RequestParam(defaultValue = "1") int pageNum,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        return bookingService.filterByStatus(status, pageNum, pageSize);
    }
}
