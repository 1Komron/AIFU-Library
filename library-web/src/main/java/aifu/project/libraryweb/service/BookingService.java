package aifu.project.libraryweb.service;


import aifu.project.common_domain.dto.BookingDiagramDTO;
import aifu.project.common_domain.dto.BookingResponse;
import aifu.project.common_domain.dto.BorrowBookDTO;
import aifu.project.common_domain.dto.ReturnBookDTO;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.payload.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {

    ResponseEntity<ResponseMessage> getBookingList(int pageNum, int pageSize);

    ResponseEntity<ResponseMessage> getBooking(Long id);

    ResponseEntity<ResponseMessage> filterByStatus(String status, int pageNum, int pageSize);

    ResponseEntity<ResponseMessage> borrowBook(BorrowBookDTO request);

    ResponseEntity<ResponseMessage> returnBook(ReturnBookDTO request);

    long countAllBookings();

    BookingDiagramDTO getBookingDiagram();

    List<BookingResponse> getListBookingsToday(int i, int pageSize, Status status);

    boolean hasBookingForUser(Long userId);

    void setStudentService(StudentService studentService);
}
