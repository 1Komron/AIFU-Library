package aifu.project.chatbot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {
    private MessageUtil() {
    }

    public static String get(String key, String lang) {
        Locale locale = switch (lang) {
            case "en" -> Locale.ENGLISH;
            case "ru" -> Locale.forLanguageTag("ru");
            case "zh" -> Locale.forLanguageTag("zh");
            default -> Locale.forLanguageTag("uz");
        };

        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
        return messages.getString(key);
    }

    public static SendMessage createMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }

    public static DeleteMessage deleteMessage(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        return deleteMessage;
    }

    public static EditMessageText editMessageText(String chatId, Integer messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        return editMessageText;
    }
}
