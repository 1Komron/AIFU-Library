package aifu.project.libraryweb.controller;

import aifu.project.commondomain.payload.ResponseMessage;
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

    private static final String NOTIFICATION_DELETE = "http://localhost:8081/notification/delete";
    private static final String NOTIFICATION_UNREAD = "http://localhost:8081/notification/get/unread";
    private static final String NOTIFICATION_ALL = "http://localhost:8081/notification/get/all";

    @Value("${internal.token}")
    private String internalToken;

    public ResponseEntity<ResponseMessage> deleteNotification(Long notificationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Internal-Token", this.internalToken);
        HttpEntity<Long> requestEntity = new HttpEntity<>(notificationId, headers);

        return restTemplate.exchange(
                NOTIFICATION_DELETE,
                HttpMethod.DELETE,
                requestEntity,
                ResponseMessage.class
        );
    }

    public ResponseEntity<ResponseMessage> getUnreadNotifications(Integer pageNumber, Integer pageSize) {
        String url = NOTIFICATION_UNREAD + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Internal-Token", this.internalToken);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ResponseMessage.class
        );
    }

    public ResponseEntity<ResponseMessage> getAllNotifications(Integer pageNumber, Integer pageSize) {
        String url = NOTIFICATION_ALL + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Internal-Token", this.internalToken);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ResponseMessage.class
        );
    }
}
