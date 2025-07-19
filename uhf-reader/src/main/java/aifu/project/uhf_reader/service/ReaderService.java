package aifu.project.uhf_reader.service;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
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
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AtomicBoolean isReading = new AtomicBoolean(false);
    private final AtomicBoolean alarmActive = new AtomicBoolean(false);
    private final AtomicBoolean awaitingStop = new AtomicBoolean(false);
    private final Map<String, Long> epcCache = new ConcurrentHashMap<>();
    private static final long REPEAT_DELAY_MS = 3000;
    private final GClient client;

    public void configureEventHandlers() {
        client.onTagEpcLog = this::handleTagEpcLog;
        client.onGpiStart = this::handleGpiStart;

        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            epcCache.entrySet().removeIf(e -> now - e.getValue() > REPEAT_DELAY_MS);
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void handleTagEpcLog(String name, LogBaseEpcInfo tag) {
        if (!isReading.get()) return;
        executor.submit(() -> {
            if (tag != null && tag.getResult() == 0) {
                String epc = tag.getEpc();
                long now = System.currentTimeMillis();

                if (!epcCache.containsKey(epc)) {
                    log.info("📦 Новая метка: {} RSSI: {}", epc, tag.getRssi());
                    epcCache.put(epc, now);
                    executor.submit(this::triggerAlarm);
                }
            }
        });
    }

    private void handleGpiStart(String readerName, LogAppGpiStart info) {
        int port = info.getGpiPort();
        log.info("Port: {}", port);
        if (port == 1) {
            if (!awaitingStop.get()) {
                executor.submit(this::startInventory);
                awaitingStop.set(true);
            } else {
                awaitingStop.set(false);
            }
        } else if (port == 0) {
            if (awaitingStop.get()) {
                executor.submit(this::stopInventory);
                awaitingStop.set(false);
            } else {
                awaitingStop.set(true);
            }
        }
    }

    private void startInventory() {
        isReading.set(true);
        log.info("📡 Инвентаризация началась (START)");
    }

    private void stopInventory() {
        try {
            Thread.sleep(3000); // имитируем задержку
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        isReading.set(false);
        log.info("⛔ Инвентаризация остановлена (STOP)");
    }

    private void triggerAlarm() {
        if (alarmActive.get()) return;

        alarmActive.set(true);

        MsgAppSetGpo enable = new MsgAppSetGpo();
        enable.setGpo2(1);
        client.sendSynMsg(enable);
        log.info("🚨 Сигнализация ВКЛЮЧЕНА!");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {

        }

        MsgAppSetGpo disable = new MsgAppSetGpo();
        disable.setGpo2(0);
        client.sendSynMsg(disable);
        log.info("🔕 Сигнализация ВЫКЛЮЧЕНА!");
        alarmActive.set(false);
    }
}
