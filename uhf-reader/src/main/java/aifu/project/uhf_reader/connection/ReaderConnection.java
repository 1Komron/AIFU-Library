package aifu.project.uhf_reader.connection;

import aifu.project.uhf_reader.repository.BookCopyRepository;
import aifu.project.uhf_reader.repository.NotificationRepository;
import aifu.project.uhf_reader.service.BookingService;
import aifu.project.uhf_reader.service.ReadService;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.GServer;
import com.gg.reader.api.protocol.gx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Hashtable;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ReaderConnection {
    private GClient client = new GClient();
    private final GServer server = new GServer();
    private final ReadService readService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    private final int port;
    private final String name;

    public ReaderConnection(int port, String name,
                            RabbitTemplate rabbitTemplate,
                            BookCopyRepository bookCopyRepository,
                            NotificationRepository notificationRepository,
                            BookingService bookingService
    ) {
        this.port = port;
        this.name = name;
        this.readService = new ReadService(
                this.name,
                rabbitTemplate,
                bookCopyRepository,
                notificationRepository,
                bookingService
        );
    }

    public void initReader() {
        log.info("[{}] Reader ga ulanish (port={})...", name, port);

        if (!server.open(port)) {
            log.error("❌ [{}] Reader ga ulanib bolmadi.", name);
            scheduler.schedule(this::tryReconnect, 5, TimeUnit.SECONDS);
        } else {
            server.onGClientConnected = this::successConnected;
        }
    }

    private void successConnected(GClient client) {
        this.client = client;
        log.info("✅ [{}] Reader muvaffaqiyatli ulandi.", name);

        client.sendSynMsg(new MsgBaseStop());
        client.setSendHeartBeat(true);

        setupPower();
        setupTriggers();
        setupInventory();

        readService.configureEventHandlers(client);
        registerReconnectHandler();
    }

    @SuppressWarnings("java:S1149")
    private void setupPower() {
        MsgBaseSetPower setPower = new MsgBaseSetPower();
        Hashtable<Integer, Integer> power = new Hashtable<>();
        power.put(1, 30);
        power.put(2, 30);
        power.put(3, 30);
        power.put(4, 30);
        setPower.setDicPower(power);
        client.sendSynMsg(setPower);
        log.info("⚙️ [{}] Antennalar kuchi o‘rnatildi.", name);
    }

    private void setupTriggers() {
        MsgAppSetGpiTrigger trigger1 = new MsgAppSetGpiTrigger();
        trigger1.setGpiPort(1);
        trigger1.setTriggerStart(1);
        trigger1.setTriggerOver(0);
        trigger1.setLevelUploadSwitch(1);
        trigger1.setTriggerCommand(null);
        client.sendSynMsg(trigger1);

        MsgAppSetGpiTrigger trigger0 = new MsgAppSetGpiTrigger();
        trigger0.setGpiPort(0);
        trigger0.setTriggerStart(1);
        trigger0.setTriggerOver(0);
        trigger0.setLevelUploadSwitch(1);
        trigger0.setTriggerCommand(null);
        client.sendSynMsg(trigger0);

        log.info("🎯 [{}] GPI triggerlar sozlandi.", name);
    }

    private void setupInventory() {
        MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
        msg.setAntennaEnable(EnumG.AntennaNo_1 | EnumG.AntennaNo_2 | EnumG.AntennaNo_3 | EnumG.AntennaNo_4);
        msg.setInventoryMode(EnumG.InventoryMode_Inventory);

        ParamEpcReadTid tid = new ParamEpcReadTid();
        tid.setMode(EnumG.ParamTidMode_Auto);
        tid.setLen(6);
        msg.setReadTid(tid);

        client.sendUnsynMsg(msg);
    }

    private void registerReconnectHandler() {
        client.onDisconnected = readerName -> {
            log.warn("❌ [{}] Reader bilan bog‘lanish yo‘qoldi: {}", name, readerName);
            scheduler.schedule(this::tryReconnect, 5, TimeUnit.SECONDS);
        };
    }

    private void tryReconnect() {
        if (reconnecting.get()) {
            log.info("[{}] Bog‘lanish jarayonida...", name);
            return;
        }
        reconnecting.set(true);

        LocalDateTime now = LocalDateTime.now();
        if (isMaintenanceTime(now)) {
            log.info("[{}] Reader ulanmandi. Dam olish vaqti", name);
            reconnecting.set(false);
        } else {
            log.info("[{}] Reader ulanmagan, qayta ulashga urinish...", name);

            try {
                server.close();
            } catch (Exception ignored) {
                log.info("[{}] Server allaqachon yopilgan", name);
            }

            this.client = null;
            initReader();
            reconnecting.set(false);
        }
    }

    private boolean isMaintenanceTime(LocalDateTime now) {
        LocalTime time = now.toLocalTime();
        return now.getDayOfWeek() == DayOfWeek.SUNDAY
                || time.isAfter(LocalTime.of(22, 0))
                || time.isBefore(LocalTime.of(7, 0));
    }
}
