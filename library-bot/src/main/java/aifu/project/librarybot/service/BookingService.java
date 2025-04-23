package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.entity.Booking;
import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.enums.Status;
import aifu.project.commondomain.repository.BookCopyRepository;
import aifu.project.commondomain.repository.BookingRepository;
import aifu.project.commondomain.repository.UserRepository;
import aifu.project.librarybot.exceptions.BookCopyNotFoundException;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ExecuteUtil executeUtil;
    private final TransactionalService transactionalService;
    private final HistoryService historyService;
    private final UserRepository userRepository;

    @Transactional
    @SneakyThrows
    public boolean borrowBook(Long chatId, String inventoryNumber, String lang) {
        transactionalService.clearState(chatId);

        BookCopy bookCopy;
        try {
            bookCopy = bookCopyRepository.findByInventoryNumber(inventoryNumber)
                    .orElseThrow(() -> new BookCopyNotFoundException(inventoryNumber));
        } catch (BookCopyNotFoundException e) {
            SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.BOOK_NOT_FOUND, lang));
            executeUtil.execute(sendMessage);
            return false;
        }

        if (bookCopy.isTaken()) {
            SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.BOOK_ALREADY_TAKEN, lang));
            executeUtil.execute(sendMessage);
            return false;
        }

        bookCopy.setTaken(true);
        bookCopyRepository.save(bookCopy);

        User user = userRepository.findByChatId(chatId);
        bookingRepository.save(createBooking(user, bookCopy));

        return true;
    }

    @Transactional
    @SneakyThrows
    public boolean returnBook(Long chatId, String inventoryNumber, String lang) {
        transactionalService.clearState(chatId);

        BookCopy bookCopy;
        try {
            bookCopy = bookCopyRepository.findByInventoryNumber(inventoryNumber)
                    .orElseThrow(() -> new BookCopyNotFoundException(inventoryNumber));
        } catch (BookCopyNotFoundException e) {
            SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.BOOK_NOT_FOUND, lang));
            executeUtil.execute(sendMessage);
            return false;
        }

        List<Booking> allBookings = bookingRepository.findAllWithBooksByUser_ChatId(chatId);

        if (allBookings.isEmpty()) {
            SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.BOOKING_LIST_EMPTY, lang));
            executeUtil.execute(sendMessage);

            return false;
        }

        AtomicBoolean isTaken = new AtomicBoolean(false);
        allBookings.forEach(booking -> {
            if (booking.getBook().getId().equals(bookCopy.getId()) &&
                    (booking.getStatus() == Status.APPROVED || booking.getStatus() == Status.OVERDUE)) {
                isTaken.set(true);


                //kutibxonachiga api chiqariladi
//                bookCopy.setTaken(false);
//
//                historyService.add(booking);
//                bookingRepository.save(booking);
            }
        });

        if (!isTaken.get()) {
            SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                    MessageUtil.get(MessageKeys.BOOKING_RETURN_NOT_FOUND, lang));
            executeUtil.execute(sendMessage);
            return false;
        }

        return true;
    }

    public String getBookList(Long chatId, String lang) {
        List<Booking> allBookings = bookingRepository.findAllWithBooksByUser_ChatId(chatId);

        if (allBookings == null || allBookings.isEmpty())
            return MessageUtil.get(MessageKeys.BOOKING_LIST_EMPTY, lang);

        String template = MessageUtil.get(MessageKeys.BOOKING_INFO, lang);

        StringBuilder messageText = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        allBookings.forEach(booking -> {
            BookCopy book = booking.getBook();
            BaseBook baseBook = book.getBook();

            String givenAt = booking.getGivenAt().format(formatter);
            String dueDate = booking.getDueDate().format(formatter);

            messageText.append(String.format(template,
                    book.getInventoryNumber(),
                    baseBook.getAuthor(),
                    baseBook.getTitle(),
                    givenAt,
                    dueDate,
                    getBookingStatusMessage(booking.getStatus(), lang)));

            messageText.append("\n\n");
        });


        return messageText.toString();
    }

    public Booking createBooking(User user, BookCopy book) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBook(book);
        booking.setStatus(Status.WAITING_APPROVAL);
        return booking;
    }

    public Booking getBooking(Long chatId, Integer bookId, Status status) {
        return bookingRepository.findByBookIdAndUserChatIdAndStatus(bookId, chatId, status);
    }

    private String getBookingStatusMessage(Status status, String lang) {
        if (status == Status.WAITING_APPROVAL)
            return MessageUtil.get(MessageKeys.BOOKING_STATUS_WAITING_APPROVAL, lang);

        if (status == Status.APPROVED)
            return MessageUtil.get(MessageKeys.BOOKING_STATUS_APPROVED, lang);

        return MessageUtil.get(MessageKeys.BOOKING_STATUS_OVERDUE, lang);
    }

    public void update(Booking booking) {
        bookingRepository.save(booking);
    }

    public void delete(Booking booking) {
        bookingRepository.delete(booking);
    }
}
