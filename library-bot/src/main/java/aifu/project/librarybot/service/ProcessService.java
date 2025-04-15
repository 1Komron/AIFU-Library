package aifu.project.librarybot.service;

import aifu.project.librarybot.enums.RegistrationStep;
import aifu.project.librarybot.enums.TransactionStep;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ExecuteUtil executeUtil;
    private final ButtonService buttonService;
    private final UserService userService;
    private final UserLanguageService userLanguageService;
    private final RegisterService registerService;
    private final TransactionalService transactionalService;
    private final BookingService bookingService;


    public void processTextMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        String lang = userLanguageService.getLanguage(chatId.toString());

        if (processStart(chatId, text, message.getFrom().getLanguageCode()))
            return;

        if (processRegistration(chatId, text, lang))
            return;

        if (processInput(chatId, text, lang))
            return;

        processButtons(chatId, text, lang);
    }

    private boolean processStart(Long chatId, String text, String lang) {
        if (text.equals("/start")) {
            userLanguageService.checkLanguage(String.valueOf(chatId), lang);

            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.WELCOME_MESSAGE,
                    userLanguageService.getLanguage(String.valueOf(chatId))));

            if (userService.exists(chatId))
                userService.loginRegister(chatId);

            return true;
        }

        return false;
    }

    @SneakyThrows
    private boolean processInput(Long chatId, String text, String lang) {
        TransactionStep state = transactionalService.getState(chatId);

        if (state == null)
            return false;

        switch (state) {
            case BORROW -> {
                if (bookingService.borrowBook(chatId, text, lang)) {
                    SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                            MessageUtil.get(MessageKeys.BOOK_BORROW_WAITING_APPROVAL, lang));
                    executeUtil.execute(sendMessage);
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @SneakyThrows
    private boolean processRegistration(Long chatId, String text, String lang) {
        if (registerService.isRegistering(chatId) && registerService.getRegistrationStep(chatId) != null) {
            if (!RegistrationStep.PHONE.equals(registerService.getRegistrationStep(chatId)))
                registerService.processRegistrationStep(chatId, text, lang);
            else
                executeUtil.execute(MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get(MessageKeys.MESSAGE_INVALID_FORMAT, lang)));
            return true;
        }
        return false;
    }

    @SneakyThrows
    private void processButtons(Long chatId, String text, String lang) {
        switch (lang) {
            case "uz" -> {
                switch (text) {
                    case "Kitob olish 📥" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;

                        transactionalService.putState(chatId, TransactionStep.BORROW);

                        SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                                MessageUtil.get(MessageKeys.BOOK_SEND_INVENTORY, lang));
                        executeUtil.execute(sendMessage);
                    }

                    case "Kitob topshirish 📤" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Mendagi kitoblar 📚" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Tarix 🗞" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Mening profilim 👤" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Sozlamalar ⚙️" -> buttonService.changeLangButton(chatId);
                    default ->
                            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.MESSAGE_INVALID_FORMAT, lang));
                }
            }

            case "ru" -> {
                switch (text) {
                    case "Взять книгу 📥" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;

                        transactionalService.putState(chatId, TransactionStep.BORROW);

                        SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                                MessageUtil.get(MessageKeys.BOOK_SEND_INVENTORY, lang));
                        executeUtil.execute(sendMessage);
                    }

                    case "Вернуть книгу 📤" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Мои книги 📚" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "История 🗞" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Мой профиль 👤" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Настройки ⚙️" -> buttonService.changeLangButton(chatId);
                    default ->
                            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.MESSAGE_INVALID_FORMAT, lang));
                }
            }

            case "en" -> {
                switch (text) {
                    case "Borrow Book 📥" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;

                        transactionalService.putState(chatId, TransactionStep.BORROW);

                        SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                                MessageUtil.get(MessageKeys.BOOK_SEND_INVENTORY, lang));
                        executeUtil.execute(sendMessage);
                    }

                    case "Return Book 📤" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "My Books 📚" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "History 🗞" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "My Profile 👤" -> {
                        if (!userService.checkUserStatus(chatId, lang))
                            return;
                    }

                    case "Settings ⚙️" -> buttonService.changeLangButton(chatId);
                    default -> buttonService.getMainButtons(chatId, text);
                }
            }

            default -> buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.MESSAGE_INVALID_FORMAT, lang));
        }

    }

    public void processCallBack(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        if (data.equals("uz") || data.equals("ru") || data.equals("en")) {
            userLanguageService.setLanguage(chatId.toString(), data);
            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.LANGUAGE_CHANGED, data));
        } else if (data.startsWith("register"))
            processCallBackDataRegister(chatId, data);
        else if (data.startsWith("login")) {
            userService.registerUser(chatId, callbackQuery.getMessage().getMessageId());
        }
    }

    @SneakyThrows
    private void processCallBackDataRegister(Long chatId, String data) {
        data = data.substring("register_".length());

        String lang = userLanguageService.getLanguage(chatId.toString());

        switch (data) {
            case "name" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.NAME);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get(MessageKeys.REGISTER_ENTER_NAME, lang));

                executeUtil.execute(sendMessage);
            }
            case "surname" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.SURNAME);

                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get(MessageKeys.REGISTER_ENTER_SURNAME, lang));

                executeUtil.execute(sendMessage);
            }
            case "phone" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.PHONE);

                SendMessage sendMessage =
                        MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.BUTTON_SEND_CONTACT, lang));
                sendMessage.setReplyMarkup(KeyboardUtil.getSendContactKeyboard(lang));

                executeUtil.execute(sendMessage);
            }
            case "faculty" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.FACULTY);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get(MessageKeys.REGISTER_ENTER_FACULTY, lang));

                executeUtil.execute(sendMessage);
            }
            case "course" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.COURSE);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get(MessageKeys.REGISTER_ENTER_COURSE, lang));

                executeUtil.execute(sendMessage);
            }
            case "group" -> {
                registerService.setRegistrationStep(chatId, RegistrationStep.GROUP);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                        MessageUtil.get(MessageKeys.REGISTER_ENTER_GROUP, lang));

                executeUtil.execute(sendMessage);
            }
            case "save" -> {
                if (!registerService.saveRegistration(chatId)) {
                    SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(),
                            MessageUtil.get(MessageKeys.REGISTER_INCOMPLETE, lang));
                    executeUtil.execute(sendMessage);
                    return;
                }

                userService.saveUser(registerService.getDTO(chatId));

                Integer lastMessageId = registerService.clearRegistrationState(chatId);
                if (lastMessageId == null)
                    return;

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));

                buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.REGISTER_SAVE_MESSAGE, lang));
            }
            case "cancel" -> {
                Integer lastMessageId = registerService.clearRegistrationState(chatId);
                if (lastMessageId == null)
                    return;

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));

                buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.REGISTER_CANCEL_MESSAGE, lang));
            }
            default -> buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.REGISTER_CANCEL_MESSAGE, lang));
        }
    }

    @SneakyThrows
    public void processRegisterPhone(Message message) {
        Long chatId = message.getChatId();
        String phone = message.getContact().getPhoneNumber();

        registerService.processRegistrationStep(chatId, phone, userLanguageService.getLanguage(chatId.toString()));

        buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.PHONE_ADDED, userLanguageService.getLanguage(chatId.toString())));
    }
}
