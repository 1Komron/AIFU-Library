package aifu.project.librarybot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardUtil {
    public static ReplyKeyboardMarkup getMainMenuKeyboard(String lang) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get("button.borrow", lang)));
        row.add(new KeyboardButton(MessageUtil.get("button.return", lang)));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get("button.myBooks", lang)));
        row.add(new KeyboardButton(MessageUtil.get("button.history", lang)));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get("button.profile", lang)));
        row.add(new KeyboardButton(MessageUtil.get("button.settings", lang)));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get("button.search", lang)));
        rows.add(row);

        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getLangInlineKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("En \uD83C\uDDFA\uD83C\uDDF8");
        button.setCallbackData("en");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("Ru \uD83C\uDDF7\uD83C\uDDFA");
        button.setCallbackData("ru");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("Uz \uD83C\uDDFA\uD83C\uDDFF");
        button.setCallbackData("uz");
        row.add(button);

        rows.add(row);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;


    }

    public static void getRegisterInlineKeyboard(SendMessage sendMessage, String lang) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(MessageUtil.get("register.name", lang));
        button.setCallbackData("register_name");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get("register.surname", lang));
        button.setCallbackData("register_surname");
        row.add(button);

        rows.add(row);
        row = new ArrayList<>();

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get("register.phone", lang));
        button.setCallbackData("register_phone");
        row.add(button);

        rows.add(row);
        row = new ArrayList<>();

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get("register.faculty", lang));
        button.setCallbackData("register_faculty");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get("register.course", lang));
        button.setCallbackData("register_course");
        row.add(button);

        rows.add(row);
        row = new ArrayList<>();

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get("register.group", lang));
        button.setCallbackData("register_group");
        row.add(button);

        rows.add(row);
        keyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    public static ReplyKeyboardMarkup getSendContactKeyboard(String lang) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(MessageUtil.get("register.phone", lang));
        button.setRequestContact(true);
        row.add(button);

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row);

        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    public static EditMessageReplyMarkup editReplyMarkup(Long chatId) {
        EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup();
        replyMarkup.setChatId(chatId);
        replyMarkup.setReplyMarkup(getLangInlineKeyboard());
        return replyMarkup;
    }

    public static EditMessageText editText(Long chatId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(text);
        editMessageText.setReplyMarkup(getLangInlineKeyboard());
        return editMessageText;
    }

}
