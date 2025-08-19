package aifu.project.librarybot.service;

import aifu.project.common_domain.dto.notification_dto.NotificationExtendShortDTO;
import aifu.project.common_domain.entity.*;
import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.entity.enums.Status;
import aifu.project.common_domain.dto.PartList;
import aifu.project.librarybot.config.RabbitMQConfig;
import aifu.project.librarybot.repository.BookingRepository;
import aifu.project.librarybot.repository.NotificationRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final ExecuteUtil executeUtil;
    private final RabbitTemplate rabbitTemplate;


    @Transactional
    public void extendDeadline(String chatId, String lang, String inv, Integer messageId) throws TelegramApiException {
        Booking booking = bookingRepository.findBookingByStudent_ChatIdAndBook_InventoryNumber(Long.valueOf(chatId), inv);
        if (booking == null) {
            executeUtil.executeMessage(chatId, MessageKeys.BOOK_NOT_FOUND, lang);
            return;
        }
        //test qilish kerak
        if (booking.getDueDate().isAfter(LocalDate.now().plusDays(1))) {
            executeUtil.executeMessage(chatId, MessageKeys.BOOK_EXTEND_DENIED_TO_EARLY, lang);
            return;
        }

        Student student = booking.getStudent();
        BookCopy book = booking.getBook();
        if (notificationRepository.existsByStudentAndBookCopy(student, book)) {
            log.info("Booking vaqtini uzaytirish so'rovi allaqachon yuborilgan. BookingID: {} StudentID: {}", booking.getId(), student.getId());
            executeUtil.execute(MessageUtil.deleteMessage(chatId, messageId));
            executeUtil.executeMessage(chatId, MessageKeys.BOOKING_STATUS_WAITING_APPROVAL, lang);
            return;
        }
        Notification notification = new Notification(student, book, NotificationType.EXTEND);

        log.info("Booking vaqtini uzaytirish so'rovi. BookingID: {} StudentID: {}", booking.getId(), student.getId());

        notification = notificationRepository.save(notification);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.KEY_EXTEND,
                NotificationExtendShortDTO.toDTO(notification));

        executeUtil.execute(MessageUtil.deleteMessage(chatId, messageId));
        executeUtil.executeMessage(chatId, MessageKeys.BOOKING_STATUS_WAITING_APPROVAL, lang);

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

    public List<Booking> getOverdueBookings() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return bookingRepository.findByDueDate(tomorrow);
    }

    public List<Booking> getExpiredBookings() {
        LocalDate now = LocalDate.now();
        return bookingRepository.findExpiredBookings(now);
    }

    private String getBookingStatusMessage(Status status, String lang) {
        if (status == Status.APPROVED)
            return MessageUtil.get(MessageKeys.BOOKING_STATUS_APPROVED, lang);

        return MessageUtil.get(MessageKeys.BOOKING_STATUS_OVERDUE, lang);
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

    public void changeStatusToOverdue() {
        List<Booking> expiredBookings = getExpiredBookings();
        expiredBookings.forEach(booking -> booking.setStatus(Status.OVERDUE));

        bookingRepository.saveAll(expiredBookings);
    }
}
