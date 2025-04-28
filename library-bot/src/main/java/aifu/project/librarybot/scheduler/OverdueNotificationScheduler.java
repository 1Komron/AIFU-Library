package aifu.project.librarybot.scheduler;

import aifu.project.librarybot.service.BookingService;
import aifu.project.librarybot.service.UserLanguageService;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
@RequiredArgsConstructor
public class OverdueNotificationScheduler {
    private final BookingService bookingService;
    private final ExecuteUtil executeUtil;
    private final UserLanguageService userLanguageService;

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendOverdueNotifications() {
        bookingService.getOverdueBookings().forEach(booking -> {
            String chatId = booking.getUser().getChatId().toString();
            String lang = userLanguageService.getLanguage(chatId);
            SendMessage message = MessageUtil.createMessage(chatId, MessageUtil.get(MessageKeys.BOOKING_DUE_EXPIRING, lang));
            message.setReplyMarkup(KeyboardUtil.getExtendKeyboard(lang));
            try {
                executeUtil.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendOverdueExpiredNotifications() {
        bookingService.getExpiredBookings().forEach(booking -> {
            String chatId = booking.getUser().getChatId().toString();
            String lang = userLanguageService.getLanguage(chatId);
            SendMessage message = MessageUtil.createMessage(chatId, MessageUtil.get(MessageKeys.BOOKING_DUE_EXPIRED, lang));
            message.setReplyMarkup(KeyboardUtil.getExtendKeyboard(lang));
            try {
                executeUtil.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
