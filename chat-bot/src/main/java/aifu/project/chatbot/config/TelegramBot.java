package aifu.project.chatbot.config;

import aifu.project.chatbot.controller.UpdateController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final UpdateController updateController;

    @Value("${bot.name}")
    private String botName;

    @Override
    public void onUpdateReceived(Update update) {
        updateController.update(update);
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    public TelegramBot(@Value("${bot.token}") String token, UpdateController updateController){
        super(token);
        this.updateController = updateController;
    }
}
