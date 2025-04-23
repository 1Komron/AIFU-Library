package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.enums.Status;
import aifu.project.commondomain.payload.BorrowBookResponse;
import aifu.project.commondomain.payload.RegistrationResponse;
import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.commondomain.payload.ReturnBookResponse;
import aifu.project.commondomain.repository.BookCopyRepository;
import aifu.project.librarybot.exceptions.BookCopyNotFoundException;
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
    private final HistoryService historyService;

    @Transactional
    public ResponseEntity<ResponseMessage> sendRegistrationMessage(RegistrationResponse response) {
        String chatId = response.chatId();
        String lang = userLanguageService.getLanguage(chatId);

        return Boolean.TRUE.equals(response.success())
                ? registrationAccept(Long.parseLong(chatId), lang)
                : registrationReject(Long.parseLong(chatId), lang);
    }

    @Transactional
    public ResponseEntity<ResponseMessage> borrowBookResponse(BorrowBookResponse response) {
        Long chatId = response.chatId();
        Integer bookId = response.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        Booking booking = bookingService.getBooking(chatId, bookId, Status.WAITING_APPROVAL);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, "Invalid response", null));
        }

        return Boolean.TRUE.equals(response.success())
                ? borrowBookAccept(booking, chatId, lang)
                : borrowBookReject(booking, chatId, lang, bookId);
    }

    @Transactional
    public ResponseEntity<ResponseMessage> returnBookResponse(ReturnBookResponse response) {
        Long chatId = response.chatId();
        Integer bookId = response.bookId();
        String lang = userLanguageService.getLanguage(chatId.toString());

        Booking booking = bookingService.getBooking(chatId, bookId, Status.APPROVED);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, "Invalid response", null));
        }

        return Boolean.TRUE.equals(response.success())
                ? returnBookAccept(booking, chatId, bookId, lang)
                : returnBookReject(booking, chatId, lang);
    }

    private ResponseEntity<ResponseMessage> returnBookAccept(Booking booking, Long chatId, Integer bookId, String lang) {
        BookCopy bookCopy;
        try {
            bookCopy = bookCopyRepository.findById(bookId)
                    .orElseThrow(() -> new BookCopyNotFoundException(bookId.toString()));
        } catch (BookCopyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, "Invalid response", null));
        }

        bookCopy.setTaken(false);
        bookCopyRepository.save(bookCopy);

        historyService.add(booking);
        bookingService.delete(booking);

        executeMessage(chatId.toString(),MessageKeys.BOOKING_RETURN_SUCCESS,lang);

        return ResponseEntity.ok(new ResponseMessage(true, "success", null));
    }

    private ResponseEntity<ResponseMessage> returnBookReject(Booking booking, Long chatId, String lang) {
        executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_REJECTED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "Rejected", null));
    }

    private ResponseEntity<ResponseMessage> registrationAccept(Long chatId, String lang) {
        userService.saveUser(chatId);

        executeMessage(chatId.toString(), MessageKeys.REGISTER_APPROVED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "User accepted by chatId: " + chatId, null));
    }

    private ResponseEntity<ResponseMessage> registrationReject(Long chatId, String lang) {
        userService.deleteUser(chatId);

        executeMessage(chatId.toString(), MessageKeys.REGISTER_REJECTED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "User rejected by chatId : " + chatId, null));
    }

    private ResponseEntity<ResponseMessage> borrowBookAccept(Booking booking, Long chatId, String lang) {
        booking.setStatus(Status.APPROVED);
        bookingService.update(booking);

        executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_APPROVED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "Accepted and message sent from: " + chatId, null));
    }

    private ResponseEntity<ResponseMessage> borrowBookReject(Booking booking, Long chatId, String lang, Integer bookId) {
        bookingService.delete(booking);

        BookCopy bookCopy;
        try {
            bookCopy = bookCopyRepository.findById(bookId)
                    .orElseThrow(() -> new BookCopyNotFoundException(bookId.toString()));
        } catch (BookCopyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(false, "Invalid response", null));
        }

        bookCopy.setTaken(false);
        bookCopyRepository.save(bookCopy);

        executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_REJECTED, lang);

        return ResponseEntity.ok(new ResponseMessage(true, "Rejected and message sent from: " + chatId, null));
    }


    @SneakyThrows
    private void executeMessage(String chatId, String messageKeys, String lang) {
        SendMessage message = MessageUtil.createMessage(chatId, MessageUtil.get(messageKeys, lang));
        executeUtil.execute(message);
    }

}
