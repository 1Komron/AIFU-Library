    package aifu.project.uhf_reader.service;

    import com.gg.reader.api.dal.GClient;
    import com.gg.reader.api.dal.GServer;
    import com.gg.reader.api.protocol.gx.*;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;

    import java.time.DayOfWeek;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.util.Hashtable;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ScheduledExecutorService;
    import java.util.concurrent.TimeUnit;
    import java.util.concurrent.atomic.AtomicBoolean;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class RfidService {
        private GClient client = new GClient();
        private final GServer server = new GServer();
        private final ReaderService readerService;
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final AtomicBoolean reconnecting = new AtomicBoolean(false);

        public void initReader() {
            int port = 8085;
            log.info("Reader ga ulanish...");

            if (!server.open(port)) {
                log.error("Reader ga ulanib bolamdi.");
                scheduler.schedule(this::tryReconnect, 5, TimeUnit.SECONDS);
            } else {
                server.onGClientConnected = this::successConnected;
            }
        }

        private void successConnected(GClient client) {
            this.client = client;
            TriggerService triggerService = new TriggerService(client);
            log.info("✅ Reader ga muvaffaqiyatli ulandi.");
            log.info("Reader aktiv (connectType={})", client.getConnectType());
            log.info("⛔ Reader Idle-rejimda");

            client.sendSynMsg(new MsgBaseStop());
            client.setSendHeartBeat(true);

            setupPower();
            setupTriggers();
            setupInventory();
            executor.submit(triggerService::triggerSuccess);

            readerService.configureEventHandlers(client);
            registerReconnectHandler();
        }

        @SuppressWarnings("java:S1149")
        private void setupPower() {
            MsgBaseSetPower setPower = new MsgBaseSetPower();
            Hashtable<Integer, Integer> power = new Hashtable<>();
            power.put(1, 20);
            power.put(2, 20);
            power.put(3, 20);
            power.put(4, 20);
            setPower.setDicPower(power);
            client.sendSynMsg(setPower);
            log.info("⚙️ Anntenalar kuchi o'rnatildi.");
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

            log.info("🎯 GPI triggerlar sozlandi");
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
                log.warn("❌ Reader bilan bog'lanish yo'qoldi: {}", readerName);
                scheduler.schedule(this::tryReconnect, 5, TimeUnit.SECONDS);
            };
        }

        private void tryReconnect() {
            if (reconnecting.get()) {
                log.info("Bog'lanish jarayonida...");
                return;
            }

            reconnecting.set(true);

            LocalDateTime now = LocalDateTime.now();
            if (isMaintenanceTime(now)) {
                log.info("Reader ga ulanmandi. Dam olish vaqti");
                reconnecting.set(false);
                return;
            }

            log.info("Reader ulanmagan, qayta ulashga urinish...");

            try {
                server.close();
            } catch (Exception ignored) {
                log.info("Server allaqachon yopilgan");
            }

            this.client = null;
            initReader();

            reconnecting.set(false);
        }


        private boolean isMaintenanceTime(LocalDateTime now) {
            LocalTime time = now.toLocalTime();
            return now.getDayOfWeek() == DayOfWeek.SUNDAY
                    || time.isAfter(LocalTime.of(22, 0))
                    || time.isBefore(LocalTime.of(7, 0));
        }

    }

