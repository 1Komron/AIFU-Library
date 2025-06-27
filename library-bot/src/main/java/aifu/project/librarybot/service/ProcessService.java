package aifu.project.librarybot.service;

import aifu.project.common_domain.payload.BookPartList;
import aifu.project.common_domain.payload.PartList;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
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
    private final SearchService searchService;
    private final BaseBookCategoryService categoryService;
    private final BookService bookService;

    private static final String BOOKING_LIST = "bookingList";
    private static final String HISTORY = "history";
    private static final String EXTEND = "extend";
    private static final String EXPIRING = "expiring";
    private static final String EXPIRED = "expired";
    private static final String SEARCH = "search";
    private static final String CATEGORY_LIST = "list";
    private static final String BOOK = "book";

    private static final Map<String, Map<String, Command>> COMMAND_MAP = Map.of(
            "uz", Map.of(
                    "Kitob olish 📥", Command.BORROW,
                    "Kitob topshirish 📤", Command.RETURN,
                    "Mening kitoblarim 📚", Command.MY_BOOKS,
                    "Tarix 🗞", Command.HISTORY,
                    "Mening profilim 👤", Command.PROFILE,
                    "Sozlamalar ⚙️", Command.SETTINGS,
                    "Qidirish \uD83D\uDD0D", Command.SEARCH
            ),
            "ru", Map.of(
                    "Взять книгу 📥", Command.BORROW,
                    "Вернуть книгу 📤", Command.RETURN,
                    "Мои книги 📚", Command.MY_BOOKS,
                    "История 🗞", Command.HISTORY,
                    "Мой профиль 👤", Command.PROFILE,
                    "Настройки ⚙️", Command.SETTINGS,
                    "Поиск \uD83D\uDD0D", Command.SEARCH
            ),
            "en", Map.of(
                    "Borrow Book 📥", Command.BORROW,
                    "Return Book 📤", Command.RETURN,
                    "My Books 📚", Command.MY_BOOKS,
                    "History 🗞", Command.HISTORY,
                    "My Profile 👤", Command.PROFILE,
                    "Settings ⚙️", Command.SETTINGS,
                    "Search \uD83D\uDD0D", Command.SEARCH
            ),
            "zh", Map.of(
                    "借书 📥", Command.BORROW,
                    "还书 📤", Command.RETURN,
                    "我的书 📚", Command.MY_BOOKS,
                    "历史记录 🗞", Command.HISTORY,
                    "我的资料 👤", Command.PROFILE,
                    "设置 ⚙️", Command.SETTINGS,
                    "搜索 \uD83D\uDD0D", Command.SEARCH
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

    @SneakyThrows
    private boolean processInput(Long chatId, String text, String lang) {
        if (text.equals("/menu")) {
            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.WELCOME_MESSAGE, lang));
            return true;
        }

        if (COMMAND_MAP.get(lang).containsKey(text)) {
            transactionalService.clearState(chatId);
            return false;
        }

        TransactionStep state = transactionalService.getState(chatId);

        if (state == null)
            return false;

        switch (state) {
            case BORROW -> {
                transactionalService.clearState(chatId);

                if (bookingService.borrowBook(chatId, text, lang))
                    executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOK_BORROW_WAITING_APPROVAL, lang);
                return true;
            }
            case RETURN -> {
                transactionalService.clearState(chatId);

                if (bookingService.returnBook(chatId, text, lang))
                    executeUtil.executeMessage(chatId.toString(), MessageKeys.BOOKING_WAIT_RETURN_APPROVAL, lang);
                return true;
            }
            case SEARCH -> {
                transactionalService.clearState(chatId);

                handlePagedList(() -> searchService.search(chatId, SEARCH + "|" + normalizeUserInput(text), lang, 1),
                        chatId, lang, SEARCH + "|" + text);
                return true;
            }
            default -> {
                transactionalService.clearState(chatId);
                return false;
            }
        }
    }

    @SneakyThrows
    private void processButtons(Long chatId, String text, String lang) {
        if (!isSettings(text, lang) && userService.checkUserStatus(chatId, lang)) {
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

            case SEARCH -> {
                String message = MessageUtil.get(MessageKeys.SEARCH_MESSAGE, lang);
                SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), message);
                sendMessage.setReplyMarkup(KeyboardUtil.getSearchInlineButtons(lang));
                executeUtil.execute(sendMessage);
            }

        }
    }

    @SneakyThrows
    public void processCallBack(CallbackQuery callbackQuery) {
        executeUtil.answerCallback(callbackQuery.getId());

        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        String lang = userLanguageService.getLanguage(chatId.toString());
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String text = callbackQuery.getMessage().getText();


        if (data.equals("uz") || data.equals("ru") || data.equals("en") || data.equals("zh")) {
            userLanguageService.setLanguage(chatId.toString(), data);
            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.LANGUAGE_CHANGED, data));
        } else if (data.startsWith("register")) {
            processCallBackDataRegister(chatId, data, messageId, text, lang);
        } else if (data.startsWith("login")) {
            registerService.registerUser(chatId, messageId, lang);
        } else if (data.startsWith("back_") || data.startsWith("next_")) {
            processControl(data, chatId, lang, messageId);
        } else if (data.startsWith(EXPIRING)) {
            processExpiring(chatId, lang, data);
        } else if (data.equals(EXPIRED)) {
            processExpired(chatId, lang, data);
        } else if (data.startsWith(EXTEND)) {
            processExtend(chatId, lang, data);
        } else if (data.startsWith("search_")) {
            processSearch(chatId, lang, data);
        } else if (data.startsWith("bookId_")) {
            String searchResult = searchService.getSearchResult(data.substring("bookId_".length()), lang);
            executeUtil.execute(new SendMessage(chatId.toString(), searchResult));
        }
    }

    private boolean processStart(Long chatId, String text, String lang) {
        if (text.equals("/start")) {
            userLanguageService.checkLanguage(String.valueOf(chatId), lang);

            buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.WELCOME_MESSAGE,
                    userLanguageService.getLanguage(String.valueOf(chatId))));

            if (!userService.exists(chatId))
                userService.loginRegister(chatId);

            return true;
        }

        return false;
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

    private boolean isSettings(String text, String lang) {
        return COMMAND_MAP.getOrDefault(lang, Collections.emptyMap())
                .get(text) == Command.SETTINGS;
    }

    @SneakyThrows
    private void processSearch(Long chatId, String lang, String data) {
        data = data.substring("search_".length());

        if (data.equals(SEARCH)) {
            transactionalService.putState(chatId, TransactionStep.SEARCH);
            executeUtil.executeMessage(chatId.toString(), MessageKeys.SEARCH_SEARCH_MESSAGE, lang);
        } else if (data.equals("list")) {
            SendMessage sendMessage = new SendMessage(chatId.toString(), MessageUtil.get(MessageKeys.SEARCH_CHOOSE, lang));
            sendMessage.setReplyMarkup(categoryService.getCategoryPartList(1, lang));
            executeUtil.execute(sendMessage);
        } else if (data.startsWith("category_")) {
            String finalData = data.substring("category_".length());
            handleBookSelect(() -> bookService.getBookList(finalData, 1), chatId, lang, BOOK + "|" + finalData);
        }
    }

    private void processExpired(Long chatId, String lang, String data) {
        handlePagedList(() -> bookingService.getExpiredBookList(chatId, lang, 1),
                chatId, lang, EXPIRED);
        bookingService.expiredBooking(chatId, lang);
    }

    private void processExpiring(Long chatId, String lang, String data) {
        if (data.equals(EXPIRING)) {
            handlePagedList(() -> bookingService.getExpiringBookList(chatId, lang, 1),
                    chatId, lang, EXPIRING);
            bookingService.expiringBooking(chatId, lang);
        }
    }

    @SneakyThrows
    private void processExtend(Long chatId, String lang, String data) {
        if (data.startsWith("extend_")) {
            String inv = data.substring("extend_".length());
            bookingService.createExtendReturnDeadline(chatId, lang, inv);
        }
    }

    private void processControl(String data, Long chatId, String lang, Integer messageId) {
        String[] split = data.split("_");
        String step = split[0];
        String rawType = split[1];
        int page = Integer.parseInt(split[2]);

        PageContext context = resolvePageContext(rawType, page);

        if ("next".equals(step)) {
            handleNextStep(context, chatId, lang, messageId);
        } else if ("back".equals(step)) {
            handleBackStep(context, chatId, lang, messageId);
        }
    }

    @SneakyThrows
    private void processCallBackDataRegister(Long chatId, String data, Integer messageId, String text, String lang) {
        if (userService.isInactive(chatId)) {
            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.REGISTER_WAIT, lang)));
            return;
        }
        if (userService.exists(chatId)) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.REGISTER_RE_REGISTER, lang);
            return;
        }

        data = data.substring("register_".length());

        registerService.checkRegistrationState(chatId, messageId, text);

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

                registerService.remove(chatId);
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
        String lang = userLanguageService.getLanguage(chatId.toString());

        if (userService.isInactive(chatId)) {
            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.REGISTER_WAIT, lang)));
            return;
        }
        if (userService.exists(chatId)) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.REGISTER_RE_REGISTER, lang);
            return;
        }

        String phone = message.getContact().getPhoneNumber();

        registerService.processRegistrationStep(chatId, phone, userLanguageService.getLanguage(chatId.toString()));

        buttonService.getMainButtons(chatId, MessageUtil.get(MessageKeys.PHONE_ADDED, userLanguageService.getLanguage(chatId.toString())));
    }

    private PageContext resolvePageContext(String rawType, int page) {
        String searchText = rawType.startsWith(SEARCH) ? rawType.substring(SEARCH.length() + 1) : null;
        String categoryId = rawType.startsWith(BOOK) ? rawType.split("\\|")[1] : null;

        String type;
        if (searchText != null) {
            type = SEARCH;
        } else if (categoryId != null) {
            type = BOOK;
        } else {
            type = rawType;
        }

        return new PageContext(type, searchText, categoryId, new AtomicInteger(page));
    }

    private void handleStep(PageContext ctx, Long chatId, String lang, Integer messageId, boolean next) {
        int page = next ? ctx.page.incrementAndGet() : ctx.page.decrementAndGet();

        switch (ctx.type) {
            case BOOKING_LIST ->
                    editMessagePagedList(() -> bookingService.getBookList(chatId, lang, page), chatId, lang, ctx.type, messageId);
            case HISTORY ->
                    editMessagePagedList(() -> historyService.getHistory(chatId, lang, page), chatId, lang, ctx.type, messageId);
            case EXPIRED ->
                    editMessagePagedList(() -> bookingService.getExpiredBookList(chatId, lang, page), chatId, lang, ctx.type, messageId);
            case EXPIRING ->
                    editMessagePagedList(() -> bookingService.getExpiringBookList(chatId, lang, page), chatId, lang, ctx.type, messageId);
            case SEARCH -> {
                String request = ctx.type + "|" + ctx.searchText;
                editMessagePagedList(() -> searchService.search(chatId, request, lang, page), chatId, lang, request, messageId);
            }
            case CATEGORY_LIST ->
                    editCategoryListMessage(() -> categoryService.getCategoryPartList(page, lang), chatId, messageId);
            case BOOK -> editBookSelect(() -> bookService.getBookList(ctx.categoryId, page), chatId, lang,
                    BOOK + "|" + ctx.categoryId, messageId);
            default -> throw new IllegalStateException("Unexpected value: " + ctx.type);
        }
    }

    @SneakyThrows
    private void handlePagedList(Supplier<PartList> supplier, Long chatId, String lang, String keyboardType) {
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
    private void editMessagePagedList(Supplier<PartList> supplier, Long chatId, String lang, String keyboardType, Integer messageId) {
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

    @SneakyThrows
    private void editCategoryListMessage(Supplier<InlineKeyboardMarkup> supplier, Long chatId, Integer messageId) {
        InlineKeyboardMarkup markup = supplier.get();
        if (markup == null) return;

        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setReplyMarkup(markup);
        edit.setChatId(chatId);
        edit.setMessageId(messageId);

        executeUtil.execute(edit);
    }

    private void handleNextStep(PageContext ctx, Long chatId, String lang, Integer messageId) {
        handleStep(ctx, chatId, lang, messageId, true);
    }

    private void handleBackStep(PageContext ctx, Long chatId, String lang, Integer messageId) {
        handleStep(ctx, chatId, lang, messageId, false);
    }

    @SneakyThrows
    private void handleBookSelect(Supplier<BookPartList> supplier, Long chatId, String lang, String keyboardType) {
        BookPartList part = supplier.get();
        if (part == null) return;

        String text = part.list();
        SendMessage msg = MessageUtil.createMessage(chatId.toString(), text);

        InlineKeyboardMarkup controlMarkup =
                KeyboardUtil.controlInlineKeyboard(lang, part.currentPage(), part.totalPages(), keyboardType);

        InlineKeyboardMarkup markup = KeyboardUtil.mergeInlineMarkups((InlineKeyboardMarkup) part.markup(), controlMarkup);

        msg.setReplyMarkup(markup);
        executeUtil.execute(msg);
    }

    @SneakyThrows
    private void editBookSelect(Supplier<BookPartList> supplier, Long chatId, String lang, String keyboardType, Integer messageId) {
        BookPartList part = supplier.get();
        if (part == null) return;

        String text = part.list();
        EditMessageText editMessageText = MessageUtil.editMessageText(chatId.toString(), messageId, text);

        InlineKeyboardMarkup controlMarkup =
                KeyboardUtil.controlInlineKeyboard(lang, part.currentPage(), part.totalPages(), keyboardType);

        InlineKeyboardMarkup markup = KeyboardUtil.mergeInlineMarkups((InlineKeyboardMarkup) part.markup(), controlMarkup);

        editMessageText.setReplyMarkup(markup);
        executeUtil.execute(editMessageText);
    }

    private record PageContext(String type, String searchText, String categoryId, AtomicInteger page) {
    }

    private String normalizeUserInput(String input) {
        return input.replaceAll("[^\\p{L}\\p{Nd} ]+", "").trim().toLowerCase();
    }
}


