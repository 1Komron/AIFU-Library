package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.booking_dto.BorrowBookDTO;
import aifu.project.common_domain.dto.booking_dto.ExtendBookingDTO;
import aifu.project.common_domain.dto.booking_dto.ReturnBookDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.BookingService;
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

    @GetMapping("/student/{id}")
    public ResponseEntity<ResponseMessage> getBookingByStudentId(@PathVariable Long id,
                                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return bookingService.getBookingByStudentId(id, pageNum, pageSize);
    }

    @PostMapping("/borrow")
    public ResponseEntity<ResponseMessage> borrowBook(@RequestBody BorrowBookDTO request) {
        return bookingService.borrowBook(request);
    }

    @PostMapping("/extend")
    public ResponseEntity<ResponseMessage> extendBooking(@RequestBody ExtendBookingDTO request) {
        return bookingService.extendBooking(request);
    }

    @PostMapping("/return")
    public ResponseEntity<ResponseMessage> returnBook(@RequestBody ReturnBookDTO request) {
        return bookingService.returnBook(request);
    }
}
