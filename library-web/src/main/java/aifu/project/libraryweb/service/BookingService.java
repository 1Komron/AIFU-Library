package aifu.project.libraryweb.service;


import aifu.project.common_domain.dto.BorrowBookDTO;
import aifu.project.common_domain.dto.ReturnBookDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import org.springframework.http.ResponseEntity;

public interface BookingService {

    ResponseEntity<ResponseMessage> getBookingList(int pageNum, int pageSize);

    ResponseEntity<ResponseMessage> getBooking(Long id);

    ResponseEntity<ResponseMessage> filterByStatus(String status, int pageNum, int pageSize);

    ResponseEntity<ResponseMessage> borrowBook(BorrowBookDTO request);

    ResponseEntity<ResponseMessage> returnBook(ReturnBookDTO request);
}
