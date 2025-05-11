package aifu.project.libraryweb.service;

import aifu.project.commondomain.payload.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ResponseService {
    private final RestTemplate restTemplate;
    private final NotificationSender notificationSender;

    private static final String URL_BOT_BORROW = "http://localhost:8081/response/book/borrow";
    private static final String URL_BOT_RETURN = "http://localhost:8081/response/book/return";
    private static final String URL_BOT_EXTEND = "http://localhost:8081/response/book/extend";
    private static final String URL_BOT_REGISTRATION = "http://localhost:8081/response/book/registration";
    private static final String NOTIFICATION_DELETE = "http://localhost:8081/notification/delete";
    private static final String SUCCESS = "success";

    public ResponseEntity<ResponseMessage> sendRegistrationMessage(RegistrationResponseWeb registrationResponse) {
        restTemplate.put(URL_BOT_REGISTRATION, registrationResponse);
        notificationSender.send(registrationResponse.notificationId());
        deleteNotification(registrationResponse.notificationId());
        return ResponseEntity.ok().body(new ResponseMessage(true, SUCCESS, null));
    }

    public ResponseEntity<ResponseMessage> borrowBookResponse(BorrowBookResponseWeb borrowBookResponse) {
        restTemplate.put(URL_BOT_BORROW, borrowBookResponse);
        notificationSender.send(borrowBookResponse.notificationId());
        deleteNotification(borrowBookResponse.notificationId());
        return ResponseEntity.ok().body(new ResponseMessage(true, SUCCESS, null));
    }

    public ResponseEntity<ResponseMessage> extendBookResponse(ExtendBookResponseWeb extendBookResponse) {
        restTemplate.put(URL_BOT_RETURN, extendBookResponse);
        notificationSender.send(extendBookResponse.notificationId());
        deleteNotification(extendBookResponse.notificationId());
        return ResponseEntity.ok().body(new ResponseMessage(true, SUCCESS, null));
    }

    public ResponseEntity<ResponseMessage> returnBookResponse(ReturnBookResponseWeb response) {
        restTemplate.put(URL_BOT_EXTEND, response);
        notificationSender.send(response.notificationId());
        deleteNotification(response.notificationId());
        return ResponseEntity.ok().body(new ResponseMessage(true, SUCCESS, null));
    }

    private void deleteNotification(Long notificationId) {
        restTemplate.put(NOTIFICATION_DELETE, notificationId);
    }
}
