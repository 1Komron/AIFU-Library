package aifu.project.uhf_reader.service;

import aifu.project.common_domain.dto.notification_dto.NotificationWarningShortDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.exceptions.BookCopyNotFoundException;
import aifu.project.uhf_reader.config.RabbitMQConfig;
import aifu.project.uhf_reader.repository.BookCopyRepository;
import aifu.project.uhf_reader.repository.NotificationRepository;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.MsgAppSetGpo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReaderService {
    private final RabbitTemplate rabbitTemplate;
    private final BookCopyRepository bookCopyRepository;
    private final NotificationRepository notificationRepository;
    private final BookingService bookingService;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AtomicBoolean alarmActive = new AtomicBoolean(false);
    private final Map<String, Long> epcCache = new ConcurrentHashMap<>();
    private static final long REPEAT_DELAY_MS = 3000;
    private final GClient client;

    public void configureEventHandlers() {
        client.onTagEpcLog = this::handleTagEpcLog;

        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            epcCache.entrySet().removeIf(e -> now - e.getValue() > REPEAT_DELAY_MS);
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void handleTagEpcLog(String name, LogBaseEpcInfo tag) {
        executor.submit(() -> {
            if (tag != null && tag.getResult() == 0) {
                String epc = tag.getEpc();
                long now = System.currentTimeMillis();

                if (!epcCache.containsKey(epc)) {
                    log.info("Tag: {} RSSI: {}", epc, tag.getRssi());
                    epcCache.put(epc, now);

                    switch (bookingService.isEpcBooked(epc)) {
                        case -1 -> {
                            executor.submit(this::triggerSuccess);
                            log.info("Tag:  '{}' not found in BookCopy table. Ignoring scan.", epc);
                        }

                        case 0 -> {
                            executor.submit(this::triggerAlarm);
                            executor.submit(() -> sendNotification(epc));
                            log.info("RFID EPC '{}' found, but no active booking exists for this book.", epc);
                        }

                        case 1 -> {
                            executor.submit(this::triggerSuccess);
                            log.info("RFID EPC '{}' is currently booked. Access allowed.", epc);
                        }

                        default -> log.error("Unexpected booking status for RFID EPC '{}'.", epc);
                    }
                }
            }
        });
    }

    private void triggerSuccess() {
        MsgAppSetGpo gpo = new MsgAppSetGpo();
        gpo.setGpo1(1);
        client.sendSynMsg(gpo);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            gpo.setGpo1(0);
            client.sendSynMsg(gpo);
            Thread.currentThread().interrupt();
        }

        gpo.setGpo1(0);
        client.sendSynMsg(gpo);
    }

    private void triggerAlarm() {
        if (alarmActive.get()) return;

        alarmActive.set(true);

        MsgAppSetGpo gpo = new MsgAppSetGpo();
        gpo.setGpo2(1);
        client.sendSynMsg(gpo);
        log.info("🚨 Сигнализация ВКЛЮЧЕНА!");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
            gpo.setGpo2(0);
            client.sendSynMsg(gpo);
            Thread.currentThread().interrupt();
        }

        gpo.setGpo2(0);
        client.sendSynMsg(gpo);
        log.info("🔕 Сигнализация ВЫКЛЮЧЕНА!");
        alarmActive.set(false);
    }

    public void sendNotification(String epc) {
        BookCopy bookCopy = bookCopyRepository.findByEpc(epc)
                .orElseThrow(() -> new BookCopyNotFoundException("Book copy not found for EPC: " + epc));

        Notification notification = new Notification(null, bookCopy, NotificationType.WARNING);
        Notification save = notificationRepository.save(notification);

        NotificationWarningShortDTO notificationDTO = NotificationWarningShortDTO.toDTO(save);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.KEY_WARNING,
                notificationDTO
        );

        log.info("RFID EPC '{}' not booked. Sending notification.", epc);
    }
}
