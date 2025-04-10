package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.repository.UserRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageUtil;
import aifu.project.librarybot.utils.UserLanguageProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ExecuteUtil executeUtil;

    public boolean exists(Long chatId) {
        return userRepository.existsUserByChatId(chatId);
    }

    @SneakyThrows
    public void registerUser(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(MessageUtil.get("register.message", UserLanguageProperties.getLanguage(chatId.toString())));

        KeyboardUtil.getRegisterInlineKeyboard(sendMessage, UserLanguageProperties.getLanguage(chatId.toString()));

        executeUtil.execute(sendMessage);
    }
}
