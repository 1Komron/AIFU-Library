package aifu.project.chatbot.controller;

import aifu.project.chatbot.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
@RequiredArgsConstructor
public class UpdateController {
    private final ProcessService processService;

    @SneakyThrows
    public void update(Update update) {
        Message message = update.getMessage();

        if (message != null && message.getText() != null)
                processService.processText(message);
    }
}
