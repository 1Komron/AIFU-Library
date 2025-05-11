package aifu.project.chatbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class ProcessService {
    public void processText(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();


    }
}
