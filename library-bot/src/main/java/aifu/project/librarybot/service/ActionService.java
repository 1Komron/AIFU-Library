package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.BookingRequest;
import aifu.project.common_domain.entity.RegisterRequest;
import aifu.project.common_domain.entity.enums.BookingRequestStatus;
import aifu.project.common_domain.entity.enums.RequestType;
import aifu.project.common_domain.payload.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActionService {
    private final UserLanguageService userLanguageService;
    private final BookingRequestService bookingRequestService;
    private final RegisterRequestService registerRequestService;
    private final ActionHandlerService actionHandlerService;
    private final NotificationService notificationService;

    static final String INVALID_RESPONSE = "Invalid response";

    public ResponseEntity<ResponseMessage> registerStudent(RegistrationRequest request) {
        String chatId = request.chatId();
        String lang = userLanguageService.getLanguage(chatId);

        RegisterRequest response = registerRequestService.getRegisterRequest(chatId);
        if (response == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        Long notificationId = notificationService.getNotificationId(response.getId(), RequestType.REGISTER);
        if (notificationId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        return Boolean.TRUE.equals(request.accept())
                ? actionHandlerService.registrationAccept(Long.parseLong(chatId), lang, notificationId, response)
                : actionHandlerService.registrationReject(Long.parseLong(chatId), lang, notificationId, response);
    }

    public ResponseEntity<ResponseMessage> borrowBookResponse(BorrowBookRequest request) {
        Long chatId = request.chatId();
        Integer bookId = request.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        BookingRequest bookingRequest = bookingRequestService.getBookingRequest(chatId, bookId, BookingRequestStatus.BORROW);
        if (bookingRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        Long notificationId = notificationService.getNotificationId(bookingRequest.getId(), RequestType.BOOKING);
        if (notificationId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        return Boolean.TRUE.equals(request.accept())
                ? actionHandlerService.borrowBookAccept(bookingRequest, chatId, lang, notificationId)
                : actionHandlerService.borrowBookReject(bookingRequest, chatId, lang, notificationId);
    }

    public ResponseEntity<ResponseMessage> extendBookResponse(ExtendBookRequest request) {
        Long chatId = request.chatId();
        Integer bookId = request.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        BookingRequest bookingRequest = bookingRequestService.getBookingRequest(chatId, bookId, BookingRequestStatus.EXTEND);
        if (bookingRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        Long notificationId = notificationService.getNotificationId(bookingRequest.getId(), RequestType.BOOKING);
        if (notificationId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        return Boolean.TRUE.equals(request.accept())
                ? actionHandlerService.extendBookAccept(bookId, bookingRequest, chatId, lang, notificationId)
                : actionHandlerService.extendBookReject(bookingRequest, chatId, lang, notificationId);
    }

    public ResponseEntity<ResponseMessage> returnBookResponse(ReturnBookRequest request) {
        Long chatId = request.chatId();
        Integer bookId = request.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        BookingRequest bookingRequest = bookingRequestService.getBookingRequest(chatId, bookId, BookingRequestStatus.RETURN);
        if (bookingRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        Long notificationId = notificationService.getNotificationId(bookingRequest.getId(), RequestType.BOOKING);
        if (notificationId == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        return Boolean.TRUE.equals(request.accept())
                ? actionHandlerService.returnBookAccept(bookingRequest, chatId, lang, notificationId)
                : actionHandlerService.returnBookReject(bookingRequest, chatId, lang, notificationId);
    }
}
