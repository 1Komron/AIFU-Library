package aifu.project.librarybot.service;

import aifu.project.librarybot.utils.UserLanguageProperties;
import org.springframework.stereotype.Service;


@Service
public class UserLanguageService {

    public String getUserLanguage(String userId) {
        return UserLanguageProperties.getLanguage(userId);
    }

    public void setUserLanguage(String userId, String language) {
        UserLanguageProperties.setLanguage(userId, language);
    }

    public void checkLanguage(String userId, String lang) {
        String userLanguage = getUserLanguage(userId);

        if (userLanguage == null)
            setUserLanguage(userId, lang);
    }
}
