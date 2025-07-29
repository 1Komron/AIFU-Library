package aifu.project.librarybot.scheduler;

import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.Student;
import aifu.project.librarybot.service.BookingService;
import aifu.project.librarybot.service.UserLanguageService;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
@Log4j2
@RequiredArgsConstructor
public class OverdueNotificationScheduler {
    private final BookingService bookingService;
    private final ExecuteUtil executeUtil;
    private final UserLanguageService userLanguageService;

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Tashkent")
    @Transactional
    public void sendOverdueExpiringNotifications() {
        List<Booking> overdueBookings = bookingService.getOverdueBookings();

        Set<String> chatIds = new HashSet<>();

        for (Booking booking : overdueBookings) {
            Student student = booking.getStudent();

            if (student == null || student.getChatId() == null) continue;

            String chatId = student.getChatId().toString();
            chatIds.add(chatId);
        }

        chatIds.forEach(chatId -> {
            String lang = userLanguageService.getLanguage(chatId);

            SendMessage message = MessageUtil.createMessage(chatId, MessageUtil.get(MessageKeys.BOOKING_DUE_EXPIRING, lang));
            message.setReplyMarkup(KeyboardUtil.getExtendKeyboard(lang, "expiring"));
            try {
                executeUtil.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Tashkent")
    @Transactional
    public void sendOverdueExpiredNotifications() {
        List<Booking> expiredBookings = bookingService.getExpiredBookings();

        Set<String> chatIds = new HashSet<>();

        for (Booking booking : expiredBookings) {
            Student student = booking.getStudent();

            if (student == null || student.getChatId() == null) continue;

            String chatId = student.getChatId().toString();
            chatIds.add(chatId);
        }

        chatIds.forEach(chatId -> {
            String lang = userLanguageService.getLanguage(chatId);

            SendMessage message = MessageUtil.createMessage(
                    chatId,
                    MessageUtil.get(MessageKeys.BOOKING_DUE_EXPIRED, lang)
            );
            message.setReplyMarkup(KeyboardUtil.getExtendKeyboard(lang, "expired"));

            try {
                executeUtil.execute(message);
            } catch (TelegramApiException e) {
                log.error("Failed to send overdue notification. chatId={}, lang={}", chatId, lang, e);
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Tashkent")
    @Transactional
    public void changBookingStatus(){
        bookingService.changeStatusToOverdue();
    }


}
