package aifu.project.librarybot.service;

import aifu.project.librarybot.utils.UserLanguageProperties;
import org.springframework.stereotype.Service;


@Service
public class UserLanguageService {

    public String getLanguage(String chatId) {
        String language = UserLanguageProperties.getLanguage(chatId);
        if (language == null) {
            setLanguage(chatId, "ru");
            return UserLanguageProperties.getLanguage(chatId);
        }
        return language;
    }

    public void setLanguage(String userId, String language) {
        UserLanguageProperties.setLanguage(userId, language);
    }

    public void checkLanguage(String userId, String lang) {
        String userLanguage = getLanguage(userId);

        if (userLanguage == null)
            setLanguage(userId, lang);
    }
}
