package aifu.project.librarybot.service;

import aifu.project.librarybot.enums.RegistrationStep;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final ExecuteUtil executeUtil;
    private final ButtonService buttonService;
    private final UserService userService;
    private final UserLanguageService userLanguageService;
    private final RegisterService registerService;

    @SneakyThrows
    public void textMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        String lang = userLanguageService.getLanguage(chatId.toString());

        if (text.equals("/start")) {
            userLanguageService.checkLanguage(String.valueOf(chatId), message.getFrom().getLanguageCode());

            buttonService.getMainButtons(chatId, MessageUtil.get("welcome.message",
                    userLanguageService.getLanguage(String.valueOf(chatId))));

            if (userService.exists(chatId))
                userService.loginRegister(chatId);

            return;
        }

        if (registerService.isRegistering(chatId) && registerService.getRegistrationStep(chatId) != null) {
            registerService.processRegistrationStep(chatId, text, lang);
            return;
        }


        switch (lang) {
            case "uz" -> {
                switch (text) {
                    case "Kitob olish 📥" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Kitob topshirish 📤" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Mendagi kitoblar 📚" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Tarix 🗞" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Mening profilim 👤" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Sozlamalar ⚙️" -> buttonService.changeLangButton(chatId);
                    default -> buttonService.getMainButtons(chatId, MessageUtil.get("message.invalid.format", lang));
                }
            }

            case "ru" -> {
                switch (text) {
                    case "Взять книгу 📥" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Вернуть книгу 📤" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Мои книги 📚" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "История 🗞" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Мой профиль 👤" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Настройки ⚙️" -> buttonService.changeLangButton(chatId);
                    default -> buttonService.getMainButtons(chatId, MessageUtil.get("message.invalid.format", lang));
                }
            }

            case "en" -> {
                switch (text) {
                    case "Borrow Book 📥" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Return Book 📤" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "My Books 📚" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "History 🗞" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "My Profile 👤" -> {
                        if (userService.isInactive(chatId)) {
                            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.wait", lang)));
                        } else if (userService.exists(chatId))
                            userService.loginRegister(chatId);
                    }
                    case "Settings ⚙️" -> buttonService.changeLangButton(chatId);
                    default -> buttonService.getMainButtons(chatId, text);
                }
            }

            default -> buttonService.getMainButtons(chatId, MessageUtil.get("message.invalid.format", lang));
        }

    }

    public void callBack(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        if (data.equals("uz") || data.equals("ru") || data.equals("en")) {
            userLanguageService.setLanguage(chatId.toString(), data);
            buttonService.getMainButtons(chatId, MessageUtil.get("language.changed", data));
        } else if (data.startsWith("register"))
            callBackDataRegister(chatId, data);
        else if (data.startsWith("login")) {
            userService.registerUser(chatId, callbackQuery.getMessage().getMessageId());
        }
    }

    @SneakyThrows
    private void callBackDataRegister(Long chatId, String data) {
        data = data.substring("register_".length());

        String lang = userLanguageService.getLanguage(chatId.toString());

        switch (data) {
            case "name" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.NAME);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get("register.enter.name", lang));

                executeUtil.execute(sendMessage);
            }
            case "surname" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.SURNAME);

                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get("register.enter.surname", lang));

                executeUtil.execute(sendMessage);
            }
            case "phone" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.PHONE);

                SendMessage sendMessage =
                        MessageUtil.createMessage(chatId.toString(), MessageUtil.get("button.sendContact", lang));
                sendMessage.setReplyMarkup(KeyboardUtil.getSendContactKeyboard(lang));

                executeUtil.execute(sendMessage);
            }
            case "faculty" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.FACULTY);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get("register.enter.faculty", lang));

                executeUtil.execute(sendMessage);
            }
            case "course" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.COURSE);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get("register.enter.course", lang));

                executeUtil.execute(sendMessage);
            }
            case "group" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.GROUP);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get("register.enter.group", lang));

                executeUtil.execute(sendMessage);
            }
            case "save" -> {
                if (!registerService.saveRegistration(chatId)) {
                    SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), MessageUtil.get("register.incomplete", lang));
                    executeUtil.execute(sendMessage);
                    return;
                }

                userService.saveUser(registerService.getDTO(chatId));

                Integer lastMessageId = registerService.clearRegistrationState(chatId);
                if (lastMessageId == null)
                    return;

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));

                buttonService.getMainButtons(chatId, MessageUtil.get("register.save.message", lang));
            }
            case "cancel" -> {
                Integer lastMessageId = registerService.clearRegistrationState(chatId);
                if (lastMessageId == null)
                    return;

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));

                buttonService.getMainButtons(chatId, MessageUtil.get("register.cancel.message", lang));
            }
            default -> throw new RuntimeException();
        }
    }

    @SneakyThrows
    public void registerPhone(Message message) {
        Long chatId = message.getChatId();
        String phone = message.getContact().getPhoneNumber();

        registerService.processRegistrationStep(chatId, phone, userLanguageService.getLanguage(chatId.toString()));

        buttonService.getMainButtons(chatId, MessageUtil.get("phone.added", userLanguageService.getLanguage(chatId.toString())));
    }
}
