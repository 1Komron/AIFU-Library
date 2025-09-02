package aifu.project.uhf_reader.control;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.MsgAppSetEthernetIP;

public class RelocateReaderToMainNetwork {

    public static void main(String[] args) {
        GClient client = new GClient();

        System.out.println("Подключаюсь к ридеру по адресу 192.168.1.250:8160...");
        if (client.openTcp("192.168.1.250:8160", 2000)) {
            System.out.println("✅ Успешно подключился к ридеру.");

            MsgAppSetEthernetIP netMsg = new MsgAppSetEthernetIP();

            netMsg.setAutoIp(1);

            netMsg.setiP("10.166.159.251");

            netMsg.setMask("255.255.224.0");

            netMsg.setGateway("10.166.128.1");

            netMsg.setDns1("10.166.128.1");

            netMsg.setDns2("8.8.8.8");

            System.out.println("Отправляю команду для 'переселения' ридера в основную сеть...");
            client.sendSynMsg(netMsg);

            if (netMsg.getRtCode() == 0x00) {
                System.out.println("🎉 ПОБЕДА! Ридер успешно 'переехал' в основную сеть.");
                System.out.println("Его новый постоянный IP-адрес: 10.166.159.250");
                System.out.println("Теперь он может выходить в интернет.");
            } else {
                System.err.println("❌ Ошибка при смене настроек: " + netMsg.getRtMsg());
            }

            client.close();

        } else {
            System.err.println("Не удалось подключиться к ридеру. Убедись, что он в режиме СЕРВЕРА по адресу 192.168.1.250.");
        }
    }
}
