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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@EnableScheduling
public class ReadService {
    private final RabbitTemplate rabbitTemplate;
    private final BookCopyRepository bookCopyRepository;
    private final NotificationRepository notificationRepository;
    private final BookingService bookingService;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AtomicBoolean alarmActive = new AtomicBoolean(false);
    private final Map<String, Long> epcCache = new ConcurrentHashMap<>();
    private final static long REPEAT_DELAY_MS = 3000;

    private TriggerService triggerService;

    public void configureEventHandlers(GClient client) {
        client.onTagEpcLog = this::handleTagEpcLog;

        this.triggerService = new TriggerService(client);

        executor.submit(triggerService::triggerSuccess);

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
                        case -1 -> log.info("Tag:  '{}' BookCopy table da topilmadi. Scan ignor qilindi.", epc);

                        case 0 -> {
                            if (alarmActive.get()) {
                                alarmActive.set(true);

                                executor.submit(triggerService::triggerAlarm);

                                alarmActive.set(false);
                            }

                            executor.submit(() -> sendNotification(epc));
                            log.info("Tag: '{}' topildi, lekin ushbu kitob boyicha aktiv bron mavjud emas.", epc);
                        }

                        case 1 -> {
                            executor.submit(triggerService::triggerSuccess);
                            log.info("Tag: '{}' aktiv bron mavjud.", epc);
                        }

                        default -> log.error("Noto'gri status kirib keldi. Tag: '{}'.", epc);
                    }
                }
            }
        });
    }

    public void sendNotification(String epc) {
        BookCopy bookCopy = bookCopyRepository.findByEpc(epc)
                .orElseThrow(() -> new BookCopyNotFoundException("Book copy topilmadi. EPC: " + epc));

        Notification notification = notificationRepository.save(new Notification(null, bookCopy, NotificationType.WARNING));

        NotificationWarningShortDTO notificationDTO = NotificationWarningShortDTO.toDTO(notification);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.KEY_WARNING,
                notificationDTO
        );

        log.info("RFID EPC '{}' bron qilinmagan. Notification uzatamiz.", epc);
    }

    public ReadService(RabbitTemplate rabbitTemplate, BookCopyRepository bookCopyRepository,
                       NotificationRepository notificationRepository, BookingService bookingService) {
        this.rabbitTemplate = rabbitTemplate;
        this.bookCopyRepository = bookCopyRepository;
        this.notificationRepository = notificationRepository;
        this.bookingService = bookingService;
    }
}
