package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.exceptions.UserDeletionException;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.librarybot.enums.TransactionStep;
import aifu.project.librarybot.repository.UserRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static aifu.project.common_domain.exceptions.UserNotFoundException.NOT_FOUND_BY_CHAT_ID;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserLanguageService userLanguageService;
    private final BookingService bookingService;
    private final BookingRequestService bookingRequestService;
    private final ExecuteUtil executeUtil;
    private final TransactionalService transactionalService;

    public boolean exists(Long chatId) {
        return userRepository.existsUserByChatIdAndIsActiveTrueAndIsDeletedFalse(chatId);
    }

    @SneakyThrows
    public void sendLoginButton(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(MessageUtil.get(MessageKeys.LOGIN, userLanguageService.getLanguage(chatId.toString())));

        KeyboardUtil.getLoginRegisterInlineKeyboard(sendMessage, userLanguageService.getLanguage(chatId.toString()));

        executeUtil.execute(sendMessage);
    }

    public void sendLoginMessage(Long chatId, String lang) {
        transactionalService.putState(chatId, TransactionStep.LOGIN);
        executeUtil.executeMessage(chatId.toString(), MessageKeys.PASSPORT_REQUEST_MESSAGE, lang);
    }


    public void login(Long chatId, String text, String lang) {
        User user = userRepository.findByIsDeletedFalseAndPassportCode(text);
        if (user != null) {
            saveUserChatId(user, chatId);
            executeUtil.executeMessage(chatId.toString(), MessageKeys.LOGIN_SUCCESS_MESSAGE, lang);
        } else {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.LOGIN_ERROR_NOT_FOUND, lang);
        }
    }

    @SneakyThrows
    public boolean checkUserStatus(Long chatId) {
        if (!exists(chatId)) {
            sendLoginButton(chatId);
            return true;
        }
        return false;
    }

    public String showProfile(Long chatId, String lang) {
        User user = userRepository.findByChatIdAndIsDeletedFalse(chatId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + chatId));

        String template = MessageUtil.get(MessageKeys.PROFILE, lang);
        return String.format(template, user.getId(), user.getName(), user.getSurname(), user.getFaculty(), user.getDegree(), user.getChatId());
    }

    public ResponseEntity<ResponseMessage> deleteUser(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + userId));

        if (bookingService.hasBookingForUser(userId))
            throw new UserDeletionException("The user cannot be deleted because he has active book reservations.");

        if (bookingRequestService.hasRequestForUser(userId))
            throw new UserDeletionException("The user cannot be deleted because they have outstanding book checkout or return requests.");

        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(new ResponseMessage(true, "User successfully deleted", null));
    }

    private void saveUserChatId(User user, Long chatId) {
        user.setChatId(chatId);
        user.setActive(true);
        userRepository.save(user);
    }


}
