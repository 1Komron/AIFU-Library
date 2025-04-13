package aifu.project.librarybot.controller;

import aifu.project.librarybot.service.ButtonService;
import aifu.project.librarybot.service.UserLanguageService;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.service.UpdateService;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
@RequiredArgsConstructor
public class UpdateController {
    private final ButtonService buttonService;
    private final UpdateService updateService;
    private final UserLanguageService langService;

    @SneakyThrows
    public void update(Update update) {
        Message message = update.getMessage();

        if (message != null) {
            if (!message.hasText() && !message.hasContact()) {
                Long chatId = message.getChatId();
                buttonService.getMainButtons(chatId, MessageUtil.get("message.invalid.format",
                        langService.getLanguage(chatId.toString())));
            } else if (message.hasContact())
                updateService.registerPhone(message);
            else
                updateService.textMessage(message);
        } else if (update.getCallbackQuery() != null && update.hasCallbackQuery())
            updateService.callBack(update.getCallbackQuery());

    }
}
