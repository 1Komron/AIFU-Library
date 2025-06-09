package aifu.project.libraryweb.service.bot_Service;

import aifu.project.commondomain.payload.*;
import aifu.project.libraryweb.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ActionService {
    private final RestTemplate restTemplate;
    private final NotificationSender notificationSender;

    @Value("${internal.token}")
    private String token;

    private static final String URL_BOT_BORROW = "http://localhost:8081/action/book/borrow";
    private static final String URL_BOT_RETURN = "http://localhost:8081/action/book/return";
    private static final String URL_BOT_EXTEND = "http://localhost:8081/action/book/extend";
    private static final String URL_BOT_REGISTRATION = "http://localhost:8081/action/registration";

    @Transactional
    public ResponseEntity<ResponseMessage> registerResponse(RegistrationRequest request) {
        ResponseEntity<ResponseMessage> apiResponse = post(request, URL_BOT_REGISTRATION);
        ResponseMessage responseBody = apiResponse.getBody();
        if (apiResponse.getStatusCode().is2xxSuccessful() && responseBody != null && responseBody.data() != null)
            notificationSender.send(((Number) responseBody.data()).longValue());

        return apiResponse;
    }

    @Transactional
    public ResponseEntity<ResponseMessage> borrowBookResponse(BorrowBookRequest request) {
        ResponseEntity<ResponseMessage> apiResponse = post(request, URL_BOT_BORROW);
        ResponseMessage responseBody = apiResponse.getBody();
        if (apiResponse.getStatusCode().is2xxSuccessful() && responseBody != null && responseBody.data() != null)
            notificationSender.send(((Number) responseBody.data()).longValue());

        return apiResponse;
    }

    @Transactional
    public ResponseEntity<ResponseMessage> extendBookResponse(ExtendBookRequest request) {
        ResponseEntity<ResponseMessage> apiResponse = post(request, URL_BOT_EXTEND);
        ResponseMessage responseBody = apiResponse.getBody();
        if (apiResponse.getStatusCode().is2xxSuccessful() && responseBody != null && responseBody.data() != null)
            notificationSender.send(((Number) responseBody.data()).longValue());

        return apiResponse;
    }

    @Transactional
    public ResponseEntity<ResponseMessage> returnBookResponse(ReturnBookRequest request) {
        ResponseEntity<ResponseMessage> apiResponse = post(request, URL_BOT_RETURN);
        ResponseMessage responseBody = apiResponse.getBody();
        if (apiResponse.getStatusCode().is2xxSuccessful() && responseBody != null && responseBody.data() != null)
            notificationSender.send(((Number) responseBody.data()).longValue());

        return apiResponse;
    }

    private ResponseEntity<ResponseMessage> post(Object object, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Internal-Token", this.token);
        HttpEntity<Object> request = new HttpEntity<>(object, headers);

        return restTemplate.exchange(url, HttpMethod.POST, request, ResponseMessage.class);
    }
}
