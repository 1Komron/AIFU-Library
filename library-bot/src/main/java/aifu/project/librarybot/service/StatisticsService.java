package aifu.project.librarybot.service;

import aifu.project.common_domain.dto.BookingDiagramDTO;
import aifu.project.common_domain.dto.BookingResponse;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.payload.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final BookingService bookingService;
    private final HistoryService historyService;

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

    public ResponseEntity<ResponseMessage> getBookingPerMonth(int month) {
        if (month < 1 || month > 12)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, "Invalid data", null));

        long historyQuantity = historyService.getQuantityPerMonth(month);
        long bookingQuantity = bookingService.getQuantityPerMonth(month);

        return ResponseEntity.ok(new ResponseMessage(true,
                "Booking per month: " + month,
                bookingQuantity + historyQuantity));
    }
}
