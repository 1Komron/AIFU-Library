package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.BookingRequest;
import aifu.project.commondomain.entity.RegisterRequest;
import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.librarybot.repository.BookCopyRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static aifu.project.librarybot.service.ActionService.INVALID_RESPONSE;

@Service
@RequiredArgsConstructor
public class ActionHandlerService {
    private final BookingService bookingService;
    private final BookingRequestService bookingRequestService;
    private final UserService userService;
    private final ExecuteUtil executeUtil;
    private final BookCopyRepository bookCopyRepository;
    private final NotificationService notificationService;
    private final HistoryService historyService;
    private final RegisterRequestService registerRequestService;


    //kitob qaytarish vaqtini uzaytirishni qabul qilish
    public ResponseEntity<ResponseMessage> extendBookAccept(Integer bookId, BookingRequest bookingRequest,
                                                            Long chatId, String lang, Long notificationId) {
        try {
            String inventoryNumber = bookingRequest.getBookCopy().getInventoryNumber();
            bookingService.extendReturnDeadline(chatId, bookId);
            String template = MessageUtil.get(MessageKeys.BOOK_EXTEND_ACCEPTED, lang);
            String text = String.format(template, inventoryNumber);
            SendMessage message = MessageUtil.createMessage(chatId.toString(), text);

            notificationService.deleteNotification(notificationId);

            bookingRequestService.delete(bookingRequest);

            executeUtil.execute(message);

            return ResponseEntity.ok(new ResponseMessage(true, "Accepted and message sent from: " + chatId,
                    notificationId));
        } catch (TelegramApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }

    }

    //kitob qaytarish vaqtini uzaytirishini rad etish
    public ResponseEntity<ResponseMessage> extendBookReject(BookingRequest bookingRequest, Long chatId,
                                                            String lang, Long notificationId) {
        try {
            String inventoryNumber = bookingRequest.getBookCopy().getInventoryNumber();
            String template = MessageUtil.get(MessageKeys.BOOK_EXTEND_REJECTED, lang);
            String text = String.format(template, inventoryNumber);
            SendMessage message = MessageUtil.createMessage(chatId.toString(), text);

            notificationService.deleteNotification(notificationId);

            bookingRequestService.delete(bookingRequest);

            executeUtil.execute(message);
            return ResponseEntity.ok(new ResponseMessage(true, "Rejected", notificationId));
        } catch (TelegramApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }
    }

    //kitib qaytarishni qabul qilish
    public ResponseEntity<ResponseMessage> returnBookAccept(BookingRequest bookingRequest, Long chatId,
                                                            String lang, Long notificationId) {
        try {
            BookCopy bookCopy = bookingRequest.getBookCopy();
            bookCopy.setTaken(false);
            bookCopyRepository.save(bookCopy);

            Booking booking = bookingService.getBooking(chatId, bookCopy.getId());
            historyService.add(booking);
            bookingService.delete(booking);

            notificationService.deleteNotification(notificationId);
            bookingRequestService.delete(bookingRequest);

            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_SUCCESS, lang);

            return ResponseEntity.ok(new ResponseMessage(true, "success", notificationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }
    }

    //kitob qaytarishni rad etish
    public ResponseEntity<ResponseMessage> returnBookReject(BookingRequest bookingRequest, Long chatId, String lang, Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            bookingRequestService.delete(bookingRequest);

            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_REJECTED, lang);
            return ResponseEntity.ok(new ResponseMessage(true, "Rejected", notificationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }
    }

    //Registratsiyani qabul qilish
    public ResponseEntity<ResponseMessage> registrationAccept(Long chatId, String lang,
                                                              Long notificationId, RegisterRequest registerRequest) {
        try {
            userService.saveUser(chatId);

            notificationService.deleteNotification(notificationId);

            registerRequestService.delete(registerRequest);

            executeUtil.executeMessage(chatId.toString(), MessageKeys.REGISTER_APPROVED, lang);

            return ResponseEntity.ok(new ResponseMessage(true, "Register accepted by chatId: " + chatId,
                    notificationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }
    }

    //Registratsiyani rad etish
    public ResponseEntity<ResponseMessage> registrationReject(Long chatId, String lang,
                                                              Long notificationId, RegisterRequest registerRequest) {
        try {
            userService.deleteUser(chatId);

            notificationService.deleteNotification(notificationId);

            registerRequestService.delete(registerRequest);

            executeUtil.executeMessage(chatId.toString(), MessageKeys.REGISTER_REJECTED, lang);

            return ResponseEntity
                    .ok(new ResponseMessage(true, "Register rejected by chatId : " + chatId, notificationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }
    }

    //kitob olishini qabul qilish
    public ResponseEntity<ResponseMessage> borrowBookAccept(BookingRequest bookingRequest, Long chatId, String lang,
                                                            Long notificationId) {
        try {
            bookingService.createBooking(bookingRequest);

            bookingRequestService.delete(bookingRequest);

            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_APPROVED, lang);

            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok(new ResponseMessage(true, "Accepted and message sent from: " + chatId,
                    notificationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }
    }

    //kitob olishni rad etish
    public ResponseEntity<ResponseMessage> borrowBookReject(BookingRequest bookingRequest, Long chatId, String lang, Long notificationId) {
        try {
            BookCopy bookCopy = bookingRequest.getBookCopy();
            bookCopy.setTaken(false);
            bookCopyRepository.save(bookCopy);

            notificationService.deleteNotification(notificationId);

            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_REJECTED, lang);

            return ResponseEntity.ok(new ResponseMessage(true, "Rejected and message sent from: " + chatId,
                    notificationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));
        }
    }
}
