package aifu.project.librarybot.config;

import aifu.project.librarybot.controller.UpdateController;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final UpdateController updateController;

    @Value("${bot.name}")
    private String botName;

    public TelegramBot(@Value("${bot.token}") String token, UpdateController updateController) {
        super(token);
        this.updateController = updateController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.update(update);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "Start"));
        commandList.add(new BotCommand("/menu", "Menu"));

        SetMyCommands setMyCommands = new SetMyCommands(commandList, null, null);

        execute(setMyCommands);
    }
}
