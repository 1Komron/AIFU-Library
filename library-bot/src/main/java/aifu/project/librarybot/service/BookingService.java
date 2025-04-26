package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.*;
import aifu.project.commondomain.entity.enums.BookingRequestStatus;
import aifu.project.commondomain.entity.enums.Status;
import aifu.project.commondomain.payload.PartList;
import aifu.project.commondomain.repository.BookCopyRepository;
import aifu.project.commondomain.repository.BookingRepository;
import aifu.project.commondomain.repository.UserRepository;
import aifu.project.librarybot.exceptions.BookCopyNotFoundException;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingRequestService bookingRequestService;
    private final BookCopyRepository bookCopyRepository;
    private final ExecuteUtil executeUtil;
    private final TransactionalService transactionalService;
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
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_NOT_FOUND, lang);
            return false;
        }

        if (bookCopy.isTaken()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_ALREADY_TAKEN, lang);
            return false;
        }

        bookCopy.setTaken(true);
        bookCopyRepository.save(bookCopy);

        User user = userRepository.findByChatId(chatId);
        bookingRequestService.create(user, bookCopy, BookingRequestStatus.BORROW);

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
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_NOT_FOUND, lang);
            return false;
        }

        List<Booking> allBookings = bookingRepository.findAllWithBooksByUser_ChatId(chatId);

        if (allBookings.isEmpty()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_LIST_EMPTY, lang);
            return false;
        }

        AtomicBoolean isTaken = new AtomicBoolean(false);
        allBookings.forEach(booking -> {
            if (booking.getBook().getId().equals(bookCopy.getId()) &&
                    (booking.getStatus() == Status.APPROVED || booking.getStatus() == Status.OVERDUE)) {
                isTaken.set(true);

                bookingRequestService.create(booking, BookingRequestStatus.RETURN);

                //kutibxonachiga api chiqariladi
            }
        });

        if (!isTaken.get()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_NOT_FOUND, lang);
            return false;
        }

        return true;
    }

    public PartList getBookList(Long chatId, String lang, int pageNumber) {
        Pageable pageable = PageRequest.of(--pageNumber, 3);
        Page<Booking> bookingPage = bookingRepository.findAllWithBooksByUserChatId(chatId, pageable);
        List<Booking> allBookings = bookingPage.getContent();


        if (allBookings.isEmpty()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_LIST_EMPTY, lang);
            return null;
        }

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

        return new PartList(messageText.toString(), ++pageNumber, bookingPage.getTotalPages());
    }

    public void createBooking(BookingRequest bookingRequest) {
        Booking booking = new Booking();
        booking.setUser(bookingRequest.getUser());
        booking.setBook(bookingRequest.getBookCopy());
        booking.setStatus(Status.APPROVED);

        bookingRepository.save(booking);
    }

    public Booking getBooking(Long chatId, Integer bookId) {
        return bookingRepository.findByBookIdAndUserChatId(bookId, chatId);
    }

    private String getBookingStatusMessage(Status status, String lang) {
        if (status == Status.WAITING_APPROVAL)
            return MessageUtil.get(MessageKeys.BOOKING_STATUS_WAITING_APPROVAL, lang);

        if (status == Status.APPROVED)
            return MessageUtil.get(MessageKeys.BOOKING_STATUS_APPROVED, lang);

        return MessageUtil.get(MessageKeys.BOOKING_STATUS_OVERDUE, lang);
    }

    public void delete(Booking booking) {
        bookingRepository.delete(booking);
    }
}
