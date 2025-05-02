package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.BookingRequest;
import aifu.project.commondomain.entity.enums.BookingRequestStatus;
import aifu.project.commondomain.payload.*;
import aifu.project.commondomain.repository.BookCopyRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class ResponseService {
    private final UserLanguageService userLanguageService;
    private final BookingService bookingService;
    private final UserService userService;
    private final ExecuteUtil executeUtil;
    private final BookCopyRepository bookCopyRepository;
    private final BookingRequestService bookingRequestService;
    private final HistoryService historyService;

    private static final String INVALID_RESPONSE = "Invalid response";

    @Transactional
    public ResponseEntity<ResponseMessage> sendRegistrationMessage(RegistrationResponse response) {
        String chatId = response.chatId();
        String lang = userLanguageService.getLanguage(chatId);

        return Boolean.TRUE.equals(response.accept())
                ? registrationAccept(Long.parseLong(chatId), lang)
                : registrationReject(Long.parseLong(chatId), lang);
    }

    @Transactional
    public ResponseEntity<ResponseMessage> borrowBookResponse(BorrowBookResponse response) {
        Long chatId = response.chatId();
        Integer bookId = response.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        BookingRequest bookingRequest = bookingRequestService.getBookingResponse(chatId, bookId, BookingRequestStatus.BORROW);
        if (bookingRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        bookingRequestService.delete(bookingRequest);

        return Boolean.TRUE.equals(response.accept())
                ? borrowBookAccept(bookingRequest, chatId, lang)
                : borrowBookReject(bookingRequest.getBookCopy(), chatId, lang);
    }

    @Transactional
    public ResponseEntity<ResponseMessage> extendBookResponse(ExtendBookResponse response) {
        Long chatId = response.chatId();
        Integer bookId = response.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        BookingRequest bookingRequest = bookingRequestService.getBookingResponse(chatId, bookId, BookingRequestStatus.EXTEND);
        if (bookingRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        bookingRequestService.delete(bookingRequest);

        String inventoryNumber = bookingRequest.getBookCopy().getInventoryNumber();
        return Boolean.TRUE.equals(response.accept())
                ? extendBookAccept(bookId, inventoryNumber, chatId, lang)
                : extendBookReject(inventoryNumber, chatId, lang);
    }

    @Transactional
    public ResponseEntity<ResponseMessage> returnBookResponse(ReturnBookResponse response) {
        Long chatId = response.chatId();
        Integer bookId = response.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        BookingRequest bookingRequest = bookingRequestService.getBookingResponse(chatId, bookId, BookingRequestStatus.RETURN);
        if (bookingRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, INVALID_RESPONSE, null));

        bookingRequestService.delete(bookingRequest);

        return Boolean.TRUE.equals(response.accept())
                ? returnBookAccept(bookingRequest, chatId, lang)
                : returnBookReject(chatId, lang);
    }

    @SneakyThrows
    private ResponseEntity<ResponseMessage> extendBookAccept(Integer bookId, String inventoryNumber, Long chatId, String lang) {
        bookingService.extendReturnDeadline(chatId, bookId);
        String template = MessageUtil.get(MessageKeys.BOOK_EXTEND_ACCEPTED, lang);
        String text = String.format(template, inventoryNumber);
        SendMessage message = MessageUtil.createMessage(chatId.toString(), text);
        executeUtil.execute(message);

        return ResponseEntity.ok(new ResponseMessage(true, "Accepted and message sent from: " + chatId, null));
    }

    @SneakyThrows
    private ResponseEntity<ResponseMessage> extendBookReject(String inventoryNumber, Long chatId, String lang) {
        String template = MessageUtil.get(MessageKeys.BOOK_EXTEND_REJECTED, lang);
        String text = String.format(template, inventoryNumber);
        SendMessage message = MessageUtil.createMessage(chatId.toString(), text);
        executeUtil.execute(message);

        return ResponseEntity.ok(new ResponseMessage(true, "Rejected", null));
    }

    private ResponseEntity<ResponseMessage> returnBookAccept(BookingRequest bookingRequest, Long chatId, String lang) {
        BookCopy bookCopy = bookingRequest.getBookCopy();
        bookCopy.setTaken(false);
        bookCopyRepository.save(bookCopy);

        Booking booking = bookingService.getBooking(chatId, bookCopy.getId());
        historyService.add(booking);
        bookingService.delete(booking);

        executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_SUCCESS, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "success", null));
    }

    private ResponseEntity<ResponseMessage> returnBookReject(Long chatId, String lang) {
        executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_REJECTED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "Rejected", null));
    }

    private ResponseEntity<ResponseMessage> registrationAccept(Long chatId, String lang) {
        userService.saveUser(chatId);

        executeUtil.executeMessage(chatId.toString(), MessageKeys.REGISTER_APPROVED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "User accepted by chatId: " + chatId, null));
    }

    private ResponseEntity<ResponseMessage> registrationReject(Long chatId, String lang) {
        userService.deleteUser(chatId);

        executeUtil.executeMessage(chatId.toString(), MessageKeys.REGISTER_REJECTED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "User rejected by chatId : " + chatId, null));
    }

    private ResponseEntity<ResponseMessage> borrowBookAccept(BookingRequest bookingRequest, Long chatId, String lang) {
        bookingService.createBooking(bookingRequest);
        executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_APPROVED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "Accepted and message sent from: " + chatId, null));
    }

    private ResponseEntity<ResponseMessage> borrowBookReject(BookCopy bookCopy, Long chatId, String lang) {
        bookCopy.setTaken(false);
        bookCopyRepository.save(bookCopy);

        executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_REJECTED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "Rejected and message sent from: " + chatId, null));
    }
}
