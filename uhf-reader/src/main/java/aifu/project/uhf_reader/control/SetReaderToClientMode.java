package aifu.project.uhf_reader.control;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.MsgAppSetTcpMode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetReaderToClientMode {

    private static final String IP = "10.166.159.250"; //Reader-2 -> 10.166.159.251
    private static final String PORT = "8160";
    private static final String NEW_SERVER_IP = "167.86.98.218";
    private static final int NEW_SERVER_PORT = 8085;  //Reader-2 -> 8086


    public static void main(String[] args) {
        GClient client = new GClient();
        log.info("Readerga bog'lanish: {}:{}", IP, PORT);
        if (client.openTcp(IP + ":" + PORT, 2000)) {
            log.info("Readerga bog'lanish muvaffaqiyatli: {}:{}", IP, PORT);

            MsgAppSetTcpMode setModeMsg = new MsgAppSetTcpMode();

            setModeMsg.setTcpMode(1);
            setModeMsg.setClientIp(NEW_SERVER_IP);
            setModeMsg.setClientPort(NEW_SERVER_PORT);

            log.info("Readerni client rejimiga o'tkazish: Client IP={}, Client PORT={}",
                    NEW_SERVER_IP, NEW_SERVER_PORT);
            client.sendSynMsg(setModeMsg);

            if (setModeMsg.getRtCode() == 0x00) {
                log.info("Reader muvaffaqiyatli client rejimiga o'tkazildi.");
                log.info("Endi reader yangi Client IP va PORT ga ulanadi: {}:{}", NEW_SERVER_IP, NEW_SERVER_PORT);
            } else {
                log.error("Readerni client rejimiga o'tkazishda xatolik: {}", setModeMsg.getRtMsg());
            }

            client.close();

        } else {
            log.error("Readerga ulanib bo'lmadi. Mumkin bo'lgan sabablar:");
            log.error("1. Reader o'chirilgan yoki tarmoqda emas.");
            log.error("2. Reader allaqachon mijoz rejimida va 8160 portini tinglamaydi.");
        }
    }
}
