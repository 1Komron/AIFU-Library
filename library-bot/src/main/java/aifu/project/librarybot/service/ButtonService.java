package aifu.project.librarybot.service;

import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class ButtonService {
    private final ExecuteUtil executeUtil;
    private final UserLanguageService userLanguageService;

    @SneakyThrows
    public void getMainButtons(Long chatId, String text) {
        ReplyKeyboardMarkup keyboard = KeyboardUtil.getMainMenuKeyboard(userLanguageService.getUserLanguage(String.valueOf(chatId)));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboard);
        sendMessage.setText(text);

        executeUtil.execute(sendMessage);
    }

    @SneakyThrows
    public void changeLangButton(Long chatId) {
        InlineKeyboardMarkup keyboard = KeyboardUtil.getLangInlineKeyboard();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(MessageUtil.get("language.choose", userLanguageService.getUserLanguage(String.valueOf(chatId))));

        sendMessage.setReplyMarkup(keyboard);
        executeUtil.execute(sendMessage);
    }
}
