package aifu.project.librarybot.utils;

import aifu.project.common_domain.entity.BaseBook;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        keyboardMarkup.setOneTimeKeyboard(false);
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

    public static InlineKeyboardMarkup controlInlineKeyboard(String lang, int pageNumber, int totalPages, String type) {
        if (totalPages == 1)
            return null;

        StringBuilder sb = new StringBuilder();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button;

        if (pageNumber != 1) {
            button = new InlineKeyboardButton();
            button.setText(MessageUtil.get(MessageKeys.BUTTON_BACK, lang));
            button.setCallbackData(sb.append("back_").append(type).append("_").append(pageNumber).toString());
            row.add(button);
        }

        button = new InlineKeyboardButton();
        button.setText(pageNumber + "/" + totalPages);
        button.setCallbackData("page_info");
        row.add(button);

        if (pageNumber != totalPages) {
            sb = new StringBuilder();
            button = new InlineKeyboardButton();
            button.setText(MessageUtil.get(MessageKeys.BUTTON_NEXT, lang));
            button.setCallbackData(sb.append("next_").append(type).append("_").append(pageNumber).toString());
            row.add(button);
        }

        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getExtendKeyboard(String lang, String type) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(MessageUtil.get(MessageKeys.BOOKING_EXTEND_BUTTON, lang));
        button.setCallbackData(type);
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getExtendBookingsInventoryNumber(List<String> inventoryNumbers) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button;

        int count = 0;
        for (String number : inventoryNumbers) {
            button = new InlineKeyboardButton();
            button.setText(number);
            button.setCallbackData("extend_" + number);
            if (count % 3 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
            row.add(button);
            count++;
        }
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getSearchInlineButtons(String lang) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData("search_list");
        button.setText(MessageUtil.get(MessageKeys.SEARCH_LIST_BUTTON, lang));

        row.add(button);
        rows.add(row);
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setCallbackData("search_search");
        button.setText(MessageUtil.get(MessageKeys.SEARCH_SEARCH_BUTTON, lang));
        row.add(button);
        rows.add(row);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getCategoryListInlineButtons(Map<Integer, String> categoryList) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button;

        int count = 0;
        for (Map.Entry<Integer, String> entry : categoryList.entrySet()) {
            button = new InlineKeyboardButton();
            button.setText(entry.getValue());
            button.setCallbackData("search_category_" + entry.getKey());
            row.add(button);

            if (count % 2 != 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
            count++;
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getBookSelectButtons(List<BaseBook> bookList) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button;

        for (int i = 0; i < bookList.size(); i++) {
            button = new InlineKeyboardButton();
            button.setText(String.valueOf(i + 1));
            button.setCallbackData("bookId_" + bookList.get(i).getId());
            row.add(button);
        }

        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup mergeInlineMarkups(InlineKeyboardMarkup markup1, InlineKeyboardMarkup markup2) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (markup1 != null) {
            List<List<InlineKeyboardButton>> markup1Keyboard = markup1.getKeyboard();
            rows.addAll(markup1Keyboard);
        }

        if (markup2 != null) {
            List<List<InlineKeyboardButton>> markup2Keyboard = markup2.getKeyboard();
            rows.addAll(markup2Keyboard);
        }

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
