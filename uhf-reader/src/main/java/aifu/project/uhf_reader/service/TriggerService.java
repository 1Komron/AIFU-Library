package aifu.project.uhf_reader.service;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.MsgAppSetGpo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TriggerService {
    private final GClient client;

    public void triggerSuccess() {
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

    public void triggerAlarm() {
        MsgAppSetGpo gpo = new MsgAppSetGpo();
        gpo.setGpo2(1);
        client.sendSynMsg(gpo);
        log.info("🚨 Siganlizatsiya yoqildi!");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
            gpo.setGpo2(0);
            client.sendSynMsg(gpo);
            Thread.currentThread().interrupt();
        }

        gpo.setGpo2(0);
        client.sendSynMsg(gpo);
        log.info("Siganlizatsiya o'chirildi");
    }

    public TriggerService(GClient client) {
        this.client = client;
    }
}
