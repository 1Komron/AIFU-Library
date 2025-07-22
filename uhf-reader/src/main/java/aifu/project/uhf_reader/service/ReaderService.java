package aifu.project.uhf_reader.service;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.MsgAppSetGpo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReaderService {
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
                    log.info("📦 Новая метка: {} RSSI: {}", epc, tag.getRssi());
                    epcCache.put(epc, now);

                    if (!bookingService.isEpcBooked(epc)) {
                        log.info("Ogirlgan kitob");
                        executor.submit(this::triggerAlarm);
                    } else {
                        log.info("booking qilingan kitob");
                        executor.submit(this::triggerSuccess);
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
}
