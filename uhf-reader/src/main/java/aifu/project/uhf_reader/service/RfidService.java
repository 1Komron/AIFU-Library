package aifu.project.uhf_reader.service;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.*;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Hashtable;

@Service
@RequiredArgsConstructor
public class RfidService {

    private static final Logger log = LoggerFactory.getLogger(RfidService.class);

    private final GClient client;
    private final ReaderService readerService;

    public void initReader() {
        if (client.openTcp("192.168.1.250:8160", 0)) {

            client.sendSynMsg(new MsgBaseStop());
            log.info("⛔ Reader в Idle-режиме");

            setupPower();
            setupTriggers();
            setupInventory();
            readerService.configureEventHandlers();
        } else {
            log.error("❌ Не удалось подключиться к считывателю.");
        }
    }

    private void setupPower() {
        MsgBaseSetPower setPower = new MsgBaseSetPower();
        Hashtable<Integer, Integer> power = new Hashtable<>();
        power.put(1, 30);
        power.put(2, 30);
        power.put(3, 30);
        power.put(4, 30);
        setPower.setDicPower(power);
        client.sendSynMsg(setPower);
        log.info("⚙️ Установлена мощность антенн");
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

        log.info("🎯 Настроены триггеры GPI");
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

    @PreDestroy
    public void shutdown() {
        if (client != null) {
            client.sendSynMsg(new MsgBaseStop());
            log.info("🛑 Reader остановлен при завершении приложения.");
        }
    }
}

