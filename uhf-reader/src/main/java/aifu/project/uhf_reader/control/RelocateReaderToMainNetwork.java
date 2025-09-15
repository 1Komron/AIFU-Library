package aifu.project.uhf_reader.control;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.MsgAppSetEthernetIP;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RelocateReaderToMainNetwork {
    private static final String READER_IP = "192.168.1.250:8160";
    private static final String NEW_IP = "10.166.159.250"; //Reader-2 -> 10.166.159.251
    private static final String MASK = "255.255.224.0";
    private static final String GATEWAY = "10.166.128.1";
    private static final String DNS_1 = "10.166.128.1";
    private static final String DNS_2 = "8.8.8.8";

    public static void main(String[] args) {
        GClient client = new GClient();

        log.info("Readerga bog'lanish: {}", READER_IP);
        if (client.openTcp(READER_IP, 2000)) {
            log.info("Readerga muvaffaqiyatli ulandi.");

            MsgAppSetEthernetIP netMsg = new MsgAppSetEthernetIP();

            netMsg.setAutoIp(1);

            netMsg.setiP(NEW_IP);

            netMsg.setMask(MASK);

            netMsg.setGateway(GATEWAY);

            netMsg.setDns1(DNS_1);

            netMsg.setDns2(DNS_2);

            log.info("Reader tarmoq sozlamalarini o'zgartirish: IP={}, MASK={}, GATEWAY={}, DNS1={}, DNS2={}",
                    NEW_IP, MASK, GATEWAY, DNS_1, DNS_2);
            client.sendSynMsg(netMsg);

            if (netMsg.getRtCode() == 0x00) {
                log.info("Reader tarmoq sozlamalari muvaffaqiyatli o'zgartirildi.");
                log.info("Reader endi qayta ishga tushadi va yangi IP manzilga ulanadi: {}", NEW_IP);
            } else {
                log.error("Tarmoq sozlamalarini o'zgartirishda xatolik: {}", netMsg.getRtMsg());
            }

            client.close();

        } else {
            log.error("Readerga ulanib bo'lmadi. Mumkin bo'lgan sabablar:");
            log.error("1. Reader o'chirilgan yoki tarmoqda emas.");
            log.error("2. Reader allaqachon client rejimida va 8160 portini tinglamaydi.");
        }
    }
}
