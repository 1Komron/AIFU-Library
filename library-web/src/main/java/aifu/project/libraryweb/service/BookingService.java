package aifu.project.libraryweb.service;


import aifu.project.common_domain.payload.ResponseMessage;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    ResponseEntity<ResponseMessage> getBookingList();

    ResponseEntity<ResponseMessage> getBooking(Long id);

    ResponseEntity<ResponseMessage> filterByStatus(String status);
}
