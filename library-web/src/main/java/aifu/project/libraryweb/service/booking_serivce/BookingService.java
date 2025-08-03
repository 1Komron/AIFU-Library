package aifu.project.libraryweb.service.booking_serivce;


import aifu.project.common_domain.dto.booking_dto.ExtendBookingDTO;
import aifu.project.common_domain.dto.statistic_dto.BookingDiagramDTO;
import aifu.project.common_domain.dto.booking_dto.BookingResponse;
import aifu.project.common_domain.dto.booking_dto.BorrowBookDTO;
import aifu.project.common_domain.dto.booking_dto.ReturnBookDTO;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.student_service.StudentServiceImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {

    ResponseEntity<ResponseMessage> getBookingList(int pageNum, int pageSize, String sortDirection);

    ResponseEntity<ResponseMessage> getBooking(Long id);

    ResponseEntity<ResponseMessage> search(String field, String query, String filter, int pageNum, int pageSize, String sortDirection);

    ResponseEntity<ResponseMessage> borrowBook(BorrowBookDTO request);

    ResponseEntity<ResponseMessage> returnBook(ReturnBookDTO request);

    long countAllBookings();

    BookingDiagramDTO getBookingDiagram();

    List<BookingResponse> getListBookingsToday(int i, int pageSize, Status status);

    boolean hasBookingForUser(Long userId);

    void setStudentService(StudentServiceImpl studentService);

    ResponseEntity<ResponseMessage> extendBooking(ExtendBookingDTO request);
}
