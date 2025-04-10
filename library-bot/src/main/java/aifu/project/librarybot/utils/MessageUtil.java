package aifu.project.librarybot.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {
    public static String get(String key, String lang) {
        Locale locale = switch (lang) {
            case "en" -> Locale.ENGLISH;
            case "ru" -> Locale.forLanguageTag("ru");
            default -> Locale.forLanguageTag("uz");
        };

        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
        return messages.getString(key);
    }
}
