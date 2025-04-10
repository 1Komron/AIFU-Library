package aifu.project.librarybot.controller;

import aifu.project.librarybot.service.UserLanguageService;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.service.UpdateService;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
@RequiredArgsConstructor
public class UpdateController {
    private final ExecuteUtil executeUtil;
    private final UpdateService updateService;
    private final UserLanguageService userLanguageService;

    @SneakyThrows
    public void update(Update update) {
        Message message = update.getMessage();

        if (message == null)
            return;

        if (!message.hasText() && !message.hasContact())
            executeUtil.execute(new SendMessage(message.getChatId().toString(), MessageUtil.get("message.invalid.format",
                    userLanguageService.getUserLanguage(message.getChatId().toString()))));
        else if (update.getCallbackQuery() != null && update.hasCallbackQuery())
            updateService.callBack(update.getCallbackQuery());
        else if (message.hasContact())
            updateService.registerPhone(message.getContact().getPhoneNumber());
        else
            updateService.textMessage(message);
    }
}
