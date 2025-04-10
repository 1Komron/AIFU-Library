package aifu.project.librarybot.service;

import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final ExecuteUtil executeUtil;
    private final ButtonService buttonService;
    private final UserService userService;
    private final UserLanguageService userLanguageService;

    @SneakyThrows
    public void textMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        String userLanguage = userLanguageService.getUserLanguage(chatId.toString());

        if (text.equals("/start")) {
            userLanguageService.checkLanguage(String.valueOf(chatId), message.getFrom().getLanguageCode());

            buttonService.getMainButtons(chatId, MessageUtil.get("welcome.message",
                    userLanguageService.getUserLanguage(String.valueOf(chatId))));

            if (!userService.exists(chatId))
                userService.registerUser(chatId);

            return;
        }

        switch (userLanguage) {
            case "uz" -> {
                switch (text) {
                    case "Kitob olish 📥" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Kitob topshirish 📤" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Mendagi kitoblar 📚" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Tarix 🗞" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Mening profilim 👤" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Sozlamalar ⚙️" -> buttonService.changeLangButton(chatId);
                    default -> buttonService.getMainButtons(chatId, "\u200B");
                }
            }

            case "ru" -> {
                switch (text) {
                    case "Взять книгу 📥" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Вернуть книгу 📤" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Мои книги 📚" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "История 🗞" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Мой профиль 👤" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Настройки ⚙️" -> buttonService.changeLangButton(chatId);
                    default -> buttonService.getMainButtons(chatId, "\\u200B");
                }
            }

            case "en" -> {
                switch (text) {
                    case "Borrow Book 📥" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Return Book 📤" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "My Books 📚" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "History 🗞" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "My Profile 👤" -> {
                        if (!userService.exists(chatId))
                            userService.registerUser(chatId);
                    }
                    case "Settings ⚙️" -> buttonService.changeLangButton(chatId);
                    default -> buttonService.getMainButtons(chatId, text);
                }
            }

            default -> buttonService.getMainButtons(chatId, "\u200B");
        }

    }

    public void callBack(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        System.out.println(data);

        if (data.equals("uz") || data.equals("ru") || data.equals("en")) {
            userLanguageService.setUserLanguage(chatId.toString(), data);
            buttonService.getMainButtons(chatId, MessageUtil.get("language.changed", data));
        } else if (data.startsWith("register"))
            callBackDataRegister(chatId, data);

    }

    @SneakyThrows
    private void callBackDataRegister(Long chatId, String data) {
        data = data.substring("register_".length());

        String lang = userLanguageService.getUserLanguage(chatId.toString());

        switch (data) {
            case "phone" -> {
                ReplyKeyboardMarkup sendContactKeyboard = KeyboardUtil.getSendContactKeyboard(lang);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(MessageUtil.get("button.sendContact", lang));
                sendMessage.setReplyMarkup(sendContactKeyboard);

                executeUtil.execute(sendMessage);
            }
        }
    }

    public void registerPhone(String phone) {
        System.out.println(phone);
    }
}
