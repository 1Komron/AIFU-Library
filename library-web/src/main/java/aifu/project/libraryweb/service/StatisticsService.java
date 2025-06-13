package aifu.project.libraryweb.service;

import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BaseBookServiceImpl;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final RestTemplate restTemplate;
    private final BaseBookServiceImpl bookService;
    private final UserService userService;
    private final BookCopyService bookCopyService;

    @Value("${statistics.baseUri}")
    private String uri;

    @Value("${internal.token}")
    private String internalToken;

    private static final String SIZE_QUERY = "&pageSize=";

    public ResponseEntity<ResponseMessage> countAllBookings() {
        return createRequest("/bookings/count");
    }

    public ResponseEntity<ResponseMessage> getBookingDiagram() {
        return createRequest("/bookings/diagram");
    }

    public ResponseEntity<ResponseMessage> countUsers() {
        long count = userService.countUsers();
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

    public ResponseEntity<ResponseMessage> getBookingToday(int pageNumber, int pageSize) {
        return createRequest("/bookings/today?pageNumber=" + pageNumber + SIZE_QUERY + pageSize);
    }

    public ResponseEntity<ResponseMessage> getBookingTodayOverdue(int pageNumber, int pageSize) {
        return createRequest("/bookings/today/overdue?pageNumber=" + pageNumber + SIZE_QUERY + pageSize);
    }

    public ResponseEntity<ResponseMessage> getBookingPerMonth(int month) {
        return createRequest("/bookings/perMonth?month=" + month);
    }

    private ResponseEntity<ResponseMessage> createRequest(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Internal-Token", this.internalToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(this.uri + url, HttpMethod.GET, entity, ResponseMessage.class);

    }
}
