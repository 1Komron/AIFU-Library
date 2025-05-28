package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.Notification;
import aifu.project.commondomain.entity.RegisterRequest;
import aifu.project.commondomain.entity.User;
import aifu.project.commondomain.entity.enums.NotificationType;
import aifu.project.commondomain.entity.enums.RequestType;
import aifu.project.commondomain.exceptions.UserNotFoundException;
import aifu.project.commondomain.mapper.NotificationMapper;
import aifu.project.commondomain.mapper.UserMapper;
import aifu.project.commondomain.payload.BotUserDTO;
import aifu.project.librarybot.config.RabbitMQConfig;
import aifu.project.librarybot.repository.NotificationRepository;
import aifu.project.librarybot.repository.UserRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserLanguageService userLanguageService;
    private final RegisterService registerService;
    private final RegisterRequestService registerRequestService;
    private final RabbitTemplate rabbitTemplate;
    private final ExecuteUtil executeUtil;

    public boolean exists(Long chatId) {
        return !userRepository.existsUserByChatId(chatId);
    }

    @SneakyThrows
    public void loginRegister(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(MessageUtil.get(MessageKeys.REGISTER_LOGIN, userLanguageService.getLanguage(chatId.toString())));

        KeyboardUtil.getLoginRegisterInlineKeyboard(sendMessage, userLanguageService.getLanguage(chatId.toString()));

        executeUtil.execute(sendMessage);
    }

    @SneakyThrows
    public void registerUser(Long chatId, Integer messageId) {
        String text = MessageUtil.get(MessageKeys.REGISTER_MESSAGE, userLanguageService.getLanguage(chatId.toString()));
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                text.formatted("-", "-", "-", "-", "-", "-"));

        KeyboardUtil.getRegisterInlineKeyboard(sendMessage, userLanguageService.getLanguage(chatId.toString()));

        registerService.checkHaveRegistrationState(chatId);

        DeleteMessage deleteMessage = MessageUtil.deleteMessage(chatId.toString(), messageId);

        executeUtil.execute(deleteMessage);

        Message execute = executeUtil.execute(sendMessage);

        registerService.addMessageId(chatId, execute.getMessageId());
    }

    @Transactional
    public void saveUser(BotUserDTO userDTO) {
        if (userDTO == null || userRepository.existsUserByChatId(userDTO.getChatId())) {
            return;
        }

        User user = UserMapper.fromBotDTO(userDTO);

        userRepository.save(user);

        RegisterRequest registerRequest = registerRequestService.create(user);

        Notification notification = new Notification(user, registerRequest.getId(), NotificationType.REGISTER, RequestType.REGISTER);
        notificationRepository.save(notification);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.KEY_REGISTER,
                NotificationMapper.notificationToDTO(notification));
    }

    public void saveUser(Long chatId) {
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException("User not found bu chatId: " + chatId));
        user.setActive(true);
        userRepository.save(user);
    }

    @SneakyThrows
    public boolean checkUserStatus(Long chatId, String lang) {
        if (exists(chatId)) {
            loginRegister(chatId);
            return false;
        } else if (isInactive(chatId)) {
            executeUtil.execute(MessageUtil.createMessage(chatId.toString(), MessageUtil.get(MessageKeys.REGISTER_WAIT, lang)));
            return false;
        }
        return true;
    }

    public String showProfile(Long chatId, String lang) {
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException("User not found bu chatId: " + chatId));

        String template = MessageUtil.get(MessageKeys.REGISTER_MESSAGE, lang);
        return String.format(template, user.getName(), user.getSurname(), user.getPhone(), user.getFaculty(), user.getCourse(), user.getGroup());
    }

    public boolean isInactive(Long chatId) {
        return !userRepository.existsByChatIdAndIsActive(chatId, true);
    }

    public void deleteUser(Long chatId) {
        userRepository.deleteByChatId(chatId);
    }
}
