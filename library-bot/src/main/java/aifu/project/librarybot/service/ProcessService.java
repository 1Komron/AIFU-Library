package aifu.project.librarybot.service;

import aifu.project.commondomain.payload.PartList;
import aifu.project.librarybot.enums.Command;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ExecuteUtil executeUtil;
    private final ButtonService buttonService;
    private final UserService userService;
    private final UserLanguageService userLanguageService;
    private final RegisterService registerService;
    private final TransactionalService transactionalService;
    private final HistoryService historyService;
    private final BookingService bookingService;

    private static final String BOOKING_LIST = "bookingList";
    private static final String HISTORY = "history";

    private static final Map<String, Map<String, Command>> COMMAND_MAP = Map.of(
            "uz", Map.of(
                    "Kitob olish 📥", Command.BORROW,
                    "Kitob topshirish 📤", Command.RETURN,
                    "Mening kitoblarim 📚", Command.MY_BOOKS,
                    "Tarix 🗞", Command.HISTORY,
                    "Mening profilim 👤", Command.PROFILE,
                    "Sozlamalar ⚙️", Command.SETTINGS
            ),
            "ru", Map.of(
                    "Взять книгу 📥", Command.BORROW,
                    "Вернуть книгу 📤", Command.RETURN,
                    "Мои книги 📚", Command.MY_BOOKS,
                    "История 🗞", Command.HISTORY,
                    "Мой профиль 👤", Command.PROFILE,
                    "Настройки ⚙️", Command.SETTINGS
            ),
            "en", Map.of(
                    "Borrow Book 📥", Command.BORROW,
                    "Return Book 📤", Command.RETURN,
                    "My Books 📚", Command.MY_BOOKS,
                    "History 🗞", Command.HISTORY,
                    "My Profile 👤", Command.PROFILE,
                    "Settings ⚙️", Command.SETTINGS
            ),
            "zh", Map.of(
                    "借书 📥", Command.BORROW,
                    "还书 📤", Command.RETURN,
                    "我的书 📚", Command.MY_BOOKS,
                    "历史记录 🗞", Command.HISTORY,
                    "我的资料 👤", Command.PROFILE,
                    "设置 ⚙️", Command.SETTINGS
            )
    );


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
                if (bookingService.borrowBook(chatId, text, lang))
                    executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_WAITING_APPROVAL, lang);
                return true;
            }
            case RETURN -> {
                if (bookingService.returnBook(chatId, text, lang))
                    executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_WAIT_RETURN_APPROVAL, lang);
                return true;
            }
            case SEARCH -> {
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
                executeUtil.executeMessage(chatId.toString(), MessageKeys.MESSAGE_INVALID_FORMAT, lang);
            return true;
        }
        return false;
    }

    @SneakyThrows
    private void processButtons(Long chatId, String text, String lang) {
        if (!isSettings(text, lang) && !userService.checkUserStatus(chatId, lang)) {
            return;
        }

        Command cmd = COMMAND_MAP
                .getOrDefault(lang, Collections.emptyMap())
                .get(text);

        if (cmd == null) {
            String invalid = MessageUtil.get(MessageKeys.MESSAGE_INVALID_FORMAT, lang);
            buttonService.getMainButtons(chatId, invalid);
            return;
        }

        switch (cmd) {
            case BORROW, RETURN -> {
                TransactionStep step = (cmd == Command.BORROW)
                        ? TransactionStep.BORROW
                        : TransactionStep.RETURN;
                transactionalService.putState(chatId, step);
                executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_SEND_INVENTORY, lang);
            }

            case MY_BOOKS ->
                    handlePagedList(() -> bookingService.getBookList(chatId, lang, 1), chatId, lang, BOOKING_LIST);

            case HISTORY -> handlePagedList(() -> historyService.getHistory(chatId, lang, 1), chatId, lang, HISTORY);

            case PROFILE -> {
                String profile = userService.showProfile(chatId, lang);
                SendMessage message = MessageUtil.createMessage(chatId.toString(), profile);
                executeUtil.execute(message);
            }

            case SETTINGS -> buttonService.changeLangButton(chatId);

        }
    }

    @SneakyThrows
    private void handlePagedList(
            Supplier<PartList> supplier,
            Long chatId,
            String lang,
            String keyboardType
    ) {
        PartList part = supplier.get();
        if (part == null) return;

        String text = part.list();
        SendMessage msg = MessageUtil.createMessage(chatId.toString(), text);
        InlineKeyboardMarkup markup =
                KeyboardUtil.controlInlineKeyboard(lang, part.currentPage(), part.totalPages(), keyboardType);
        if (markup != null)
            msg.setReplyMarkup(markup);
        executeUtil.execute(msg);
    }

    @SneakyThrows
    private void editMessagePagedList(
            Supplier<PartList> supplier,
            Long chatId,
            String lang,
            String keyboardType,
            Integer messageId
    ) {
        PartList part = supplier.get();
        if (part == null) return;

        String text = part.list();
        EditMessageText editMessageText = MessageUtil.editMessageText(chatId.toString(), messageId, text);
        InlineKeyboardMarkup markup =
                KeyboardUtil.controlInlineKeyboard(lang, part.currentPage(), part.totalPages(), keyboardType);
        if (markup != null)
            editMessageText.setReplyMarkup(markup);

        executeUtil.execute(editMessageText);
    }

    private boolean isSettings(String text, String lang) {
        return COMMAND_MAP.getOrDefault(lang, Collections.emptyMap())
                .get(text) == Command.SETTINGS;
    }

    public void processCallBack(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        String lang = userLanguageService.getLanguage(chatId.toString());

        if (data.equals("uz") || data.equals("ru") || data.equals("en") || data.equals("zh")) {
            userLanguageService.setLanguage(chatId.toString(), data);
            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.LANGUAGE_CHANGED, data));
        } else if (data.startsWith("register"))
            processCallBackDataRegister(chatId, data);
        else if (data.startsWith("login")) {
            userService.registerUser(chatId, callbackQuery.getMessage().getMessageId());
        } else if (data.startsWith("back_") || data.startsWith("next_")) {
            processControl(data, chatId, lang, callbackQuery.getMessage().getMessageId());
        }
    }

    private void processControl(String data, Long chatId, String lang, Integer messageId) {
        String[] split = data.split("_");
        String step = split[0];
        String type = split[1];
        AtomicInteger pageNumber = new AtomicInteger(Integer.parseInt(split[2]));

        if (step.equals("next")) {
            if (type.equals(BOOKING_LIST))
                editMessagePagedList(() -> bookingService.getBookList(chatId, lang, pageNumber.incrementAndGet()),
                        chatId, lang, type, messageId);
            else if (type.equals(HISTORY))
                editMessagePagedList(() -> historyService.getHistory(chatId, lang, pageNumber.incrementAndGet()),
                        chatId, lang, type, messageId);
        } else if (step.equals("back")) {
            if (type.equals(BOOKING_LIST))
                editMessagePagedList(() -> bookingService.getBookList(chatId, lang, pageNumber.decrementAndGet()),
                        chatId, lang, type, messageId);
            else if (type.equals(HISTORY))
                editMessagePagedList(() -> historyService.getHistory(chatId, lang, pageNumber.decrementAndGet()),
                        chatId, lang, type, messageId);
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
