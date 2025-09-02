package aifu.project.uhf_reader.control;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.MsgAppSetTcpMode;

public class SetReaderToClientMode {

    public static void main(String[] args) {
        GClient client = new GClient();
        System.out.println("Подключаюсь к ридеру по адресу 10.166.159.251:8160...");
        if (client.openTcp("10.166.159.251:8160", 2000)) {
            System.out.println("✅ Успешно подключился к ридеру.");

            MsgAppSetTcpMode setModeMsg = new MsgAppSetTcpMode();

            setModeMsg.setTcpMode(1);
            setModeMsg.setClientIp("167.86.98.218");
            setModeMsg.setClientPort(8086);

            System.out.println("Отправляю команду на переключение в клиентский режим...");
            client.sendSynMsg(setModeMsg);

            if (setModeMsg.getRtCode() == 0x00) {
                System.out.println("🎉 ПОБЕДА! Ридер успешно переведен в клиентский режим.");
                System.out.println("Он перезагрузится и будет подключаться к 167.86.98.218:8085");
            } else {
                System.err.println("❌ Ошибка при смене режима: " + setModeMsg.getRtMsg());
            }

            client.close();

        } else {
            System.err.println("Не удалось подключиться к ридеру. Возможные причины:");
            System.err.println("1. Ридер выключен или не в сети.");
            System.err.println("2. Ридер УЖЕ находится в клиентском режиме и не слушает порт 8160.");
        }
    }
}
