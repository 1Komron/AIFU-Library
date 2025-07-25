/*package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.BookingDiagramDTO;
import aifu.project.common_domain.dto.BookingResponse;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BaseBookServiceImpl;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final BaseBookServiceImpl bookService;
    private final StudentService studentService;
    private final BookCopyService bookCopyService;
    private final BookingService bookingService;

    public ResponseEntity<ResponseMessage> countAllBookings() {
        long count = bookingService.countAllBookings();
        return ResponseEntity.ok(new ResponseMessage(true, "Booking count", count));
    }

    public ResponseEntity<ResponseMessage> getBookingDiagram() {
        BookingDiagramDTO diagram = bookingService.getBookingDiagram();
        return ResponseEntity.ok(new ResponseMessage(true, "Booking diagram", diagram));
    }

    public ResponseEntity<ResponseMessage> getBookingToday(int pageNumber, int pageSize) {
        List<BookingResponse> list = bookingService.getListBookingsToday(--pageNumber, pageSize, Status.APPROVED);
        return ResponseEntity.ok(new ResponseMessage(true, "Today's booking list", list));
    }

    public ResponseEntity<ResponseMessage> getBookingTodayOverdue(int pageNumber, int pageSize) {
        List<BookingResponse> list = bookingService.getListBookingsToday(--pageNumber, pageSize, Status.OVERDUE);
        return ResponseEntity.ok(new ResponseMessage(true, "Today's booking list", list));
    }

    public ResponseEntity<ResponseMessage> countUsers() {
        long count = studentService.countStudents();
        return ResponseEntity.ok(new ResponseMessage(true, "Users count", count));
    }

    public ResponseEntity<ResponseMessage> countBooks() {
        long count = bookService.countBooks();
        return ResponseEntity.ok(new ResponseMessage(true, "Books count", count));
    }

    public ResponseEntity<ResponseMessage> countBookCopies() {
        long count = bookCopyService.count();
        return ResponseEntity.ok(new ResponseMessage(true, "Book copies count", count));
    }

    public ResponseEntity<ResponseMessage> getBookingPerMonth(int month) {
        return null;
    }
}
*/