package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.Notification;
import aifu.project.common_domain.entity.RegisterRequest;
import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.entity.enums.RequestType;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.mapper.NotificationMapper;
import aifu.project.common_domain.mapper.UserMapper;
import aifu.project.common_domain.payload.BotUserDTO;
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


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserLanguageService userLanguageService;
    private final RegisterRequestService registerRequestService;
    private final HistoryService historyService;
    private final BookingService bookingService;
    private final BookingRequestService bookingRequestService;
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

    public boolean existsUser(Long chatId) {
        return userRepository.existsUserByChatId(chatId);
    }

    @Transactional
    public String removeUser(Long chatId) {
        boolean hasHistoryForUser = historyService.hasHistoryForUserChatId(chatId);
        boolean hasBooking = bookingService.hasBookingForUserChatId(chatId);
        boolean hasRequest = bookingRequestService.hasRequestForUserChatId(chatId);

        if (hasHistoryForUser)
            return "history";

        if (hasBooking)
            return "booking";

        if (hasRequest)
            return "request";

        userRepository.deleteByChatId(chatId);
        return null;
    }

    public ResponseEntity<String> deleteUser(Long userId) {
        if (!userRepository.existsUserById(userId)) {
            return ResponseEntity.badRequest().build();
        }
        boolean hasHistoryForUser = historyService.hasHistoryForUser(userId);
        boolean hasBooking = bookingService.hasBookingForUser(userId);
        boolean hasRequest = bookingRequestService.hasRequestForUser(userId);
        boolean hasRegisterRequest = registerRequestService.hasRequestForUser(userId);

        if (hasHistoryForUser)
            return ResponseEntity.ok("history");

        if (hasBooking)
            return ResponseEntity.ok("booking");

        if (hasRequest)
            return ResponseEntity.ok("request");

        if (hasRegisterRequest)
            return ResponseEntity.ok("registerRequest");

        userRepository.deleteById(userId);
        return ResponseEntity.ok("deleted");
    }
}
