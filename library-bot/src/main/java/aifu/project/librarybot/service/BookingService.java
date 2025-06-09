package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.*;
import aifu.project.commondomain.entity.enums.BookingRequestStatus;
import aifu.project.commondomain.entity.enums.NotificationType;
import aifu.project.commondomain.entity.enums.RequestType;
import aifu.project.commondomain.entity.enums.Status;
import aifu.project.commondomain.exceptions.BookCopyNotFoundException;
import aifu.project.commondomain.exceptions.UserNotFoundException;
import aifu.project.commondomain.payload.PartList;
import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.librarybot.config.RabbitMQConfig;
import aifu.project.librarybot.repository.BookCopyRepository;
import aifu.project.librarybot.repository.BookingRepository;
import aifu.project.librarybot.repository.NotificationRepository;
import aifu.project.librarybot.repository.UserRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static aifu.project.commondomain.mapper.NotificationMapper.notificationToDTO;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookingRequestService bookingRequestService;
    private final ExecuteUtil executeUtil;
    private final TransactionalService transactionalService;
    private final RabbitTemplate rabbitTemplate;

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

        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException("User not found by chatId: " + chatId));

        BookingRequest bookingRequest = bookingRequestService.create(user, bookCopy, BookingRequestStatus.BORROW);

        Notification notification = new Notification(user, bookingRequest.getId(), NotificationType.BORROW, RequestType.BOOKING);
        notificationRepository.save(notification);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.KEY_BORROW,
                notificationToDTO(notification));

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
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_NOT_FOUND, lang);
            return false;
        }

        AtomicBoolean isTaken = new AtomicBoolean(false);
        allBookings.forEach(booking -> {
            if (booking.getBook().getId().equals(bookCopy.getId()) &&
                    (booking.getStatus() == Status.APPROVED || booking.getStatus() == Status.OVERDUE)) {
                isTaken.set(true);

                BookingRequest bookingRequest = bookingRequestService.create(booking.getUser(), bookCopy, BookingRequestStatus.RETURN);

                User user = userRepository.findByChatId(chatId)
                        .orElseThrow(() -> new UserNotFoundException("User not found by chatId: " + chatId));

                Notification notification = new Notification(user, bookingRequest.getId(), NotificationType.RETURN, RequestType.BOOKING);
                notificationRepository.save(notification);
                rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.KEY_RETURN,
                        notificationToDTO(notification));
            }
        });

        if (!isTaken.get()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_RETURN_NOT_FOUND, lang);
            return false;
        }

        return true;
    }

    @Transactional
    public void createExtendReturnDeadline(Long chatId, String lang, String inv) {
        Booking booking = bookingRepository.findBookingByUser_ChatIdAndBook_InventoryNumber(chatId, inv);
        if (booking == null) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_NOT_FOUND, lang);
            return;
        }
        if (booking.getDueDate().isAfter(LocalDate.now().plusDays(1))) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_EXTEND_DENIED_TO_EARLY, lang);
            return;
        }

        BookingRequest bookingRequest = bookingRequestService.create(booking.getUser(), booking.getBook(), BookingRequestStatus.EXTEND);
        executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_EXTEND_WAITING_APPROVAL, lang);

        User user = booking.getUser();
        Notification notification = new Notification(user, bookingRequest.getId(), NotificationType.EXTEND, RequestType.BOOKING);
        notificationRepository.save(notification);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.KEY_EXTEND,
                notificationToDTO(notification));
    }

    public PartList getBookList(Long chatId, String lang, int pageNumber) {
        Pageable pageable = PageRequest.of(--pageNumber, 3);
        Page<Booking> bookingPage = bookingRepository.findAllWithBooksByUserChatId(chatId, pageable);
        List<Booking> allBookings = bookingPage.getContent();

        if (allBookings.isEmpty()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_LIST_EMPTY, lang);
            return null;
        }
        return getPartList(allBookings, lang, pageNumber, bookingPage.getTotalPages());
    }

    public PartList getExpiredBookList(Long chatId, String lang, int pageNumber) {
        Pageable pageable = PageRequest.of(--pageNumber, 3);
        Page<Booking> bookingPage = bookingRepository.findAllExpiredOverdue(chatId, LocalDate.now(), pageable);

        List<Booking> allBookings = bookingPage.getContent();

        if (allBookings.isEmpty()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_REMINDER_NONE, lang);
            return null;
        }

        return getPartList(allBookings, lang, pageNumber, bookingPage.getTotalPages());
    }

    public PartList getExpiringBookList(Long chatId, String lang, int pageNumber) {
        Pageable pageable = PageRequest.of(--pageNumber, 3);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Page<Booking> bookingPage =
                bookingRepository.findAllExpiringOverdue(chatId, tomorrow, pageable);

        List<Booking> allBookings = bookingPage.getContent();

        if (allBookings.isEmpty()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_REMINDER_NONE, lang);
            return null;
        }

        return getPartList(allBookings, lang, pageNumber, bookingPage.getTotalPages());
    }

    private PartList getPartList(List<Booking> allBookings, String lang, int pageNumber, int totalPages) {
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

        return new PartList(messageText.toString(), ++pageNumber, totalPages);
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

    public List<Booking> getOverdueBookings() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return bookingRepository.findByDueDate(tomorrow);
    }

    public List<Booking> getExpiredBookings() {
        LocalDate now = LocalDate.now();
        List<Booking> byDueDateBefore = bookingRepository.findByDueDateBefore(now);
        byDueDateBefore.forEach(booking -> booking.setStatus(Status.OVERDUE));

        bookingRepository.saveAll(byDueDateBefore);
        return byDueDateBefore;
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


    public void expiredBooking(Long chatId, String lang) {
        List<Booking> expiredBookings = bookingRepository.findAllExpiredBookings(chatId, LocalDate.now());
        if (expiredBookings.isEmpty()) return;

        extracted(chatId, lang, expiredBookings);
    }


    public void expiringBooking(Long chatId, String lang) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Booking> expiringBookings = bookingRepository.findAllExpiringBookings(chatId, tomorrow);
        if (expiringBookings.isEmpty()) return;

        extracted(chatId, lang, expiringBookings);
    }

    @SneakyThrows
    private void extracted(Long chatId, String lang, List<Booking> list) {
        List<String> inventoryNumbers = list.stream()
                .map(Booking::getBook)
                .map(BookCopy::getInventoryNumber)
                .toList();

        SendMessage message = MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.SELECT_INV, lang));
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardUtil.getExtendBookingsInventoryNumber(inventoryNumbers);
        message.setReplyMarkup(inlineKeyboardMarkup);
        executeUtil.execute(message);
    }

    public void extendReturnDeadline(Long chatId, Integer bookId) {
        Booking booking = getBooking(chatId, bookId);
        LocalDate dueDate = booking.getDueDate();
        LocalDate now = LocalDate.now();

        LocalDate newDueDate = dueDate.isAfter(now) ? dueDate.plusDays(3) : now.plusDays(3);

        booking.setDueDate(newDueDate);
        booking.setStatus(Status.APPROVED);

        bookingRepository.save(booking);
    }

    public ResponseEntity<ResponseMessage> countAllBookings() {
        long count = bookingRepository.count();
        return ResponseEntity.ok(new ResponseMessage(true,"Booking count", count));
    }
}
