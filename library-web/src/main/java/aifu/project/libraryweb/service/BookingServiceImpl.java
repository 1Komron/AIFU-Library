package aifu.project.libraryweb.service;

import aifu.project.common_domain.payload.ResponseMessage;
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
public class BookingServiceImpl implements BookingService {
    private final RestTemplate restTemplate;

    @Value("${booking.baseUri}")
    private String bookingBaseUri;
    @Value("${internal.token}")
    private String internalToken;

    private static final String TOKEN_HEADER = "Internal-Token";

    @Override
    public ResponseEntity<ResponseMessage> getBookingList(int pageNum, int pageSize) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(TOKEN_HEADER, this.internalToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                this.bookingBaseUri + "?pageNum=" + pageNum + "&pageSize=" + pageSize,
                HttpMethod.GET,
                entity,
                ResponseMessage.class);
    }

    @Override
    public ResponseEntity<ResponseMessage> getBooking(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(TOKEN_HEADER, this.internalToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                this.bookingBaseUri + "/" + id,
                HttpMethod.GET,
                entity,
                ResponseMessage.class);
    }

    @Override
    public ResponseEntity<ResponseMessage> filterByStatus(String status, int pageNum, int pageSize) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(TOKEN_HEADER, this.internalToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                this.bookingBaseUri + "/filter?status=" + status + "&pageNum=" + pageNum + "&pageSize=" + pageSize,
                HttpMethod.GET,
                entity,
                ResponseMessage.class);
    }
}
