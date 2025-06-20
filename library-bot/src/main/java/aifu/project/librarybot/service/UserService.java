package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.entity.RegisterRequest;
import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.entity.enums.RequestType;
import aifu.project.common_domain.exceptions.UserDeletionException;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.mapper.NotificationMapper;
import aifu.project.common_domain.mapper.UserMapper;
import aifu.project.common_domain.payload.BotUserDTO;
import aifu.project.common_domain.payload.ResponseMessage;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static aifu.project.common_domain.exceptions.UserNotFoundException.NOT_FOUND_BY_CHAT_ID;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserLanguageService userLanguageService;
    private final RegisterRequestService registerRequestService;
    private final BookingService bookingService;
    private final BookingRequestService bookingRequestService;
    private final RabbitTemplate rabbitTemplate;
    private final ExecuteUtil executeUtil;

    public boolean exists(Long chatId) {
        return !userRepository.existsUserByChatIdAndIsActiveTrueAndIsDeletedFalse(chatId);
    }

    @SneakyThrows
    public void loginRegister(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(MessageUtil.get(MessageKeys.REGISTER_LOGIN, userLanguageService.getLanguage(chatId.toString())));

        KeyboardUtil.getLoginRegisterInlineKeyboard(sendMessage, userLanguageService.getLanguage(chatId.toString()));

        executeUtil.execute(sendMessage);
    }

    @Transactional
    public void saveUser(BotUserDTO userDTO) {
        if (userDTO == null || userRepository.existsUserByChatIdAndIsActiveTrueAndIsDeletedFalse(userDTO.getChatId())) {
            return;
        }

        Long id = userRepository.returnUserId(userDTO.getChatId());

        User user = UserMapper.fromBotDTO(userDTO);
        user.setId(id);
        userRepository.save(user);

        RegisterRequest registerRequest = registerRequestService.create(user);

        Notification notification = new Notification(user, registerRequest.getId(), NotificationType.REGISTER, RequestType.REGISTER);
        notificationRepository.save(notification);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.KEY_REGISTER,
                NotificationMapper.notificationToDTO(notification));
    }

    public void saveUser(Long chatId) {
        User user = userRepository.findByChatIdAndIsDeletedFalse(chatId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + chatId));
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
        User user = userRepository.findByChatIdAndIsDeletedFalse(chatId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + chatId));

        String template = MessageUtil.get(MessageKeys.REGISTER_MESSAGE, lang);
        return String.format(template, user.getName(), user.getSurname(), user.getPhone(), user.getFaculty(), user.getCourse(), user.getGroup());
    }

    public boolean isInactive(Long chatId) {
        return !userRepository.existsByChatIdAndIsActiveAndIsDeletedFalse(chatId, true);
    }

    public boolean existsUser(Long chatId) {
        return userRepository.existsUserByChatIdAndIsActiveTrueAndIsDeletedFalse(chatId);
    }

    public void removeUser(Long chatId) {
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + chatId));

        user.setActive(false);
        user.setDeleted(true);
        userRepository.save(user);
    }

    public ResponseEntity<ResponseMessage> deleteUser(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + userId));

        if (bookingService.hasBookingForUser(userId))
            throw new UserDeletionException("The user cannot be deleted because he has active book reservations.");

        if (bookingRequestService.hasRequestForUser(userId))
            throw new UserDeletionException("The user cannot be deleted because they have outstanding book checkout or return requests.");

        if (registerRequestService.hasRequestForUser(userId))
            throw new UserDeletionException("The user cannot be deleted because he has an active registration request.");

        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(new ResponseMessage(true, "User successfully deleted", null));
    }
}
