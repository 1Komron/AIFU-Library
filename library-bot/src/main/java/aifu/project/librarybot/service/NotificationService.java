package aifu.project.librarybot.service;

import aifu.project.common_domain.dto.NotificationShortDTO;
import aifu.project.common_domain.entity.*;
import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.entity.enums.RequestType;
import aifu.project.common_domain.exceptions.NotificationNotFoundException;
import aifu.project.common_domain.exceptions.RequestNotFoundException;
import aifu.project.common_domain.mapper.UserMapper;
import aifu.project.common_domain.payload.BookingRequestDTO;
import aifu.project.common_domain.payload.BotUserDTO;
import aifu.project.common_domain.payload.RegisterRequestDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.librarybot.repository.BookingRequestRepository;
import aifu.project.librarybot.repository.NotificationRepository;
import aifu.project.librarybot.repository.RegisterRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final RegisterRequestRepository registerRequestRepository;

    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.getNotificationById(notificationId);

        if (notification == null)
            throw new RuntimeException("Notification Not Found");

        notificationRepository.delete(notification);
    }

    public Long getNotificationId(Long requestId, RequestType type) {
        return notificationRepository.findNotificationIdByRequestIdAndRequestType(requestId, type);
    }

    public ResponseEntity<ResponseMessage> getUnreadNotifications(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "notificationTime"));
        Page<Notification> page = notificationRepository.findNotificationByIsRead(false, pageable);
        if (page.getTotalElements() == 0)
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ResponseMessage(false, "No unread notifications", null));

        Map<String, Object> pageInfo = Map.of(
                "pageNumber", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "data", getShortDTO(page.getContent())
        );

        return ResponseEntity.ok(new ResponseMessage(true, "All unread notifications", pageInfo));
    }

    public ResponseEntity<ResponseMessage> getAllNotifications(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "notificationTime"));
        Page<Notification> page = notificationRepository.findAll(pageable);
        if (page.getTotalElements() == 0)
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ResponseMessage(false, "Empty", null));

        Map<String, Object> pageInfo = Map.of(
                "pageNumber", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "data", getShortDTO(page.getContent())
        );

        return ResponseEntity.ok(new ResponseMessage(true, "All notifications", pageInfo));
    }

    public ResponseEntity<ResponseMessage> get(String notificationId) {
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

        return new RegisterRequestDTO(botDTO, RequestType.REGISTER);
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
                bookCopy.getInventoryNumber(),
                RequestType.BOOKING
        );
    }

    private List<NotificationShortDTO> getShortDTO(List<Notification> notifications) {
        return notifications.stream()
                .map(notification ->
                        new NotificationShortDTO(notification.getId(),
                                notification.getUserName() + " " + notification.getUserSurname(),
                                notification.getNotificationType(), notification.getNotificationTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                notification.isRead()))
                .toList();
    }

    public ResponseEntity<ResponseMessage> getNotificationByType(int pageNumber, int pageSize, String type) {
        NotificationType notificationType = NotificationType.getNotification(type);
        Pageable pageable = PageRequest.of(--pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "notificationTime"));

        Page<NotificationShortDTO> page = notificationRepository.findAllByNotificationType(notificationType, pageable);

        Map<String, Object> map = Map.of(
                "data", page.getContent(),
                "currentPage", page.getNumber() + 1,
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Notification list By: "+type, map));
    }
}
