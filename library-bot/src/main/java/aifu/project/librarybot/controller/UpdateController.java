package aifu.project.librarybot.controller;

import aifu.project.librarybot.service.ButtonService;
import aifu.project.librarybot.service.UserLanguageService;
import aifu.project.librarybot.service.ProcessService;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
@RequiredArgsConstructor
public class UpdateController {
    private final ButtonService buttonService;
    private final ProcessService processService;
    private final UserLanguageService langService;

    @SneakyThrows
    public void update(Update update) {
        Message message = update.getMessage();

        if (message != null) {
            if (!message.hasText() && !message.hasContact()) {
                Long chatId = message.getChatId();
                buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.MESSAGE_INVALID_FORMAT,
                        langService.getLanguage(chatId.toString())));
            } else if (message.hasContact())
                processService.processRegisterPhone(message);
            else
                processService.processTextMessage(message);
        } else if (update.getCallbackQuery() != null && update.hasCallbackQuery())
            processService.processCallBack(update.getCallbackQuery());

    }
}
