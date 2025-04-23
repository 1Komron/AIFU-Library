package aifu.project.librarybot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardUtil {
    private KeyboardUtil() {
    }

    public static ReplyKeyboardMarkup getMainMenuKeyboard(String lang) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get(MessageKeys.BUTTON_BORROW, lang)));
        row.add(new KeyboardButton(MessageUtil.get(MessageKeys.BUTTON_RETURN, lang)));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get(MessageKeys.BUTTON_MY_BOOKS, lang)));
        row.add(new KeyboardButton(MessageUtil.get(MessageKeys.BUTTON_HISTORY, lang)));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get(MessageKeys.BUTTON_PROFILE, lang)));
        row.add(new KeyboardButton(MessageUtil.get(MessageKeys.BUTTON_SETTINGS, lang)));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(MessageUtil.get(MessageKeys.BUTTON_SEARCH, lang)));
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
        button.setText("En \uD83C\uDDEC\uD83C\uDDE7");
        button.setCallbackData("en");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("Ru \uD83C\uDDF7\uD83C\uDDFA");
        button.setCallbackData("ru");
        row.add(button);

        rows.add(row);
        row = new ArrayList<>();

        button = new InlineKeyboardButton();
        button.setText("Uz \uD83C\uDDFA\uD83C\uDDFF");
        button.setCallbackData("uz");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("Zh \uD83C\uDDE8\uD83C\uDDF3");
        button.setCallbackData("zh");
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
        button.setText(MessageUtil.get(MessageKeys.REGISTER_NAME, lang));
        button.setCallbackData("register_name");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_SURNAME, lang));
        button.setCallbackData("register_surname");
        row.add(button);
        rows.add(row);

        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_PHONE, lang));
        button.setCallbackData("register_phone");
        row.add(button);
        rows.add(row);

        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_FACULTY, lang));
        button.setCallbackData("register_faculty");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_COURSE, lang));
        button.setCallbackData("register_course");
        row.add(button);
        rows.add(row);

        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_GROUP, lang));
        button.setCallbackData("register_group");
        row.add(button);
        rows.add(row);

        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_CANCEL_BUTTON, lang));
        button.setCallbackData("register_cancel");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_SAVE_BUTTON, lang));
        button.setCallbackData("register_save");
        row.add(button);
        rows.add(row);

        keyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    public static void getLoginRegisterInlineKeyboard(SendMessage sendMessage, String language) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.REGISTER_LOGIN_BUTTON, language));
        button.setCallbackData("login");
        row.add(button);
        rows.add(row);

        keyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    public static ReplyKeyboardMarkup getSendContactKeyboard(String lang) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(MessageUtil.get(MessageKeys.REGISTER_PHONE, lang));
        button.setRequestContact(true);
        row.add(button);

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row);

        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

}
