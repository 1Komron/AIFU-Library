package aifu.project.libraryweb.service.bot_service;

import aifu.project.common_domain.entity.*;
import aifu.project.common_domain.entity.enums.RequestType;
import aifu.project.common_domain.exceptions.NotificationNotFoundException;
import aifu.project.common_domain.exceptions.RequestNotFoundException;
import aifu.project.common_domain.mapper.UserMapper;
import aifu.project.common_domain.payload.BookingRequestDTO;
import aifu.project.common_domain.payload.BotUserDTO;
import aifu.project.common_domain.payload.RegisterRequestDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.repository.BookingRequestRepository;
import aifu.project.libraryweb.repository.NotificationRepository;
import aifu.project.libraryweb.repository.RegisterRequestRepository;
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
    private final NotificationRepository notificationRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final RegisterRequestRepository registerRequestRepository;

    private static final String NOTIFICATION_DELETE = "http://localhost:8081/notification/delete";
    private static final String NOTIFICATION_UNREAD = "http://localhost:8081/notification/get/unread";
    private static final String NOTIFICATION_ALL = "http://localhost:8081/notification/get/all";

    private static final String INTERNAL_TOKEN = "Internal-Token";

    @Value("${internal.token}")
    private String internalToken;

    public ResponseEntity<ResponseMessage> deleteNotification(Long notificationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(INTERNAL_TOKEN, this.internalToken);
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
        headers.set(INTERNAL_TOKEN, this.internalToken);

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
        headers.set(INTERNAL_TOKEN, this.internalToken);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ResponseMessage.class
        );
    }

    public ResponseEntity<ResponseMessage> getDetails(String notificationId) {
        Notification notification;
        try {
            notification = notificationRepository.findNotificationById(Long.parseLong(notificationId))
                    .orElseThrow(() -> new NotificationNotFoundException("Not found by id: " + notificationId));
        } catch (ClassCastException e) {
            throw new NotificationNotFoundException("Not found by id: " + notificationId);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        Object data = getRequestBody(notification.getRequestType(), notification.getRequestId());

        return ResponseEntity.ok(new ResponseMessage(true, "Notification detail", data));
    }

    private Object getRequestBody(RequestType type, Long requestId) {
        if (type == RequestType.BOOKING) {
            BookingRequest bookingRequest = bookingRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RequestNotFoundException("Booking request not found by requestId: " + requestId));

            return createBookingRequestDTO(bookingRequest);
        } else {
            RegisterRequest registerRequest = registerRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RequestNotFoundException("Register request not found by requestId: " + requestId));

            return createRegisterRequestDTO(registerRequest);
        }
    }

    private RegisterRequestDTO createRegisterRequestDTO(RegisterRequest registerRequest) {
        User user = registerRequest.getUser();
        BotUserDTO botDTO = UserMapper.toBotDTO(user);

        return new RegisterRequestDTO(botDTO, registerRequest.getCreatedAt());
    }

    private BookingRequestDTO createBookingRequestDTO(BookingRequest bookingRequest) {
        BookCopy bookCopy = bookingRequest.getBookCopy();
        BaseBook book = bookCopy.getBook();

        return new BookingRequestDTO(
                UserMapper.toBotDTO(bookingRequest.getUser()),
                bookCopy.getId(),
                book.getAuthor(),
                book.getTitle(),
                book.getIsbn(),
                bookCopy.getInventoryNumber()
        );
    }
}
