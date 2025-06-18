package aifu.project.libraryweb.service.bot_service;

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
public class NotificationService {
    private final RestTemplate restTemplate;

    private static final String INTERNAL_TOKEN = "Internal-Token";

    @Value("${notification.unread}")
    private String unread;

    @Value("${notification.getAll}")
    private String getAll;

    @Value("${notification.get}")
    private String get;

    @Value("${internal.token}")
    private String internalToken;


    public ResponseEntity<ResponseMessage> getUnreadNotifications(Integer pageNumber, Integer pageSize) {
        String url = this.unread + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        HttpHeaders headers = new HttpHeaders();
        headers.set(INTERNAL_TOKEN, this.internalToken);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ResponseMessage.class
        );
    }

    public ResponseEntity<ResponseMessage> getAllNotifications(Integer pageNumber, Integer pageSize) {
        String url = this.getAll + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        HttpHeaders headers = new HttpHeaders();
        headers.set(INTERNAL_TOKEN, this.internalToken);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ResponseMessage.class
        );
    }

    public ResponseEntity<ResponseMessage> getDetails(String notificationId) {
        String url = this.get + "/" + notificationId;
        HttpHeaders headers = new HttpHeaders();
        headers.set(INTERNAL_TOKEN, this.internalToken);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ResponseMessage.class
        );
    }

}
