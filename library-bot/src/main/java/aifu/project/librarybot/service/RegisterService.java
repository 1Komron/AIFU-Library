package aifu.project.librarybot.service;

import aifu.project.commondomain.payload.BotUserDTO;
import aifu.project.librarybot.enums.RegistrationStep;
import aifu.project.librarybot.session.RegistrationState;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterService {
    private final ConcurrentHashMap<Long, RegistrationState> registrationState = new ConcurrentHashMap<>();
    private final ExecuteUtil executeUtil;

    public void createRegistrationState(Long chatId) {
        registrationState.put(chatId, new RegistrationState(null, new BotUserDTO(), null));
    }

    public void checkRegistrationState(Long chatId, Integer messageId, String text) {
        registrationState.computeIfAbsent(chatId, k -> setBotUserToNewRegistrationState(chatId, messageId, text));
    }

    private RegistrationState setBotUserToNewRegistrationState(Long chatId, Integer messageId, String text) {
        BotUserDTO botUserDTO = new BotUserDTO();
        botUserDTO.setChatId(chatId);

        log.info("Restoring registration state for chatId={} from message:\n{}", chatId, text);

        String[] lines = text.split("\\R");

        if (lines.length < 6) {
            log.warn("Incomplete registration message from chatId={}: only {} lines received", chatId, lines.length);
            throw new IllegalArgumentException("Incomplete registration data.");
        }

        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(":", 2);

            String part = null;
            if (parts.length == 2) {
                part = parts[1].trim().equals("-") ? null : parts[1].trim();
            }

            switch (i) {
                case 0 -> botUserDTO.setName(part);
                case 1 -> botUserDTO.setSurname(part);
                case 2 -> botUserDTO.setPhone(part);
                case 3 -> botUserDTO.setFaculty(part);
                case 4 -> botUserDTO.setCourse(part);
                case 5 -> botUserDTO.setGroup(part);
                default -> {
                    log.error("Unexpected line in the registration message: {}", lines[i]);
                    throw new IllegalArgumentException("Error while restoring registration state: unexpected line index " + i);
                }
            }
        }

        log.info("Bot user successfully restored from message: {}", botUserDTO);
        return new RegistrationState(messageId, botUserDTO, null);
    }

    public void addMessageId(Long chatId, Integer messageId) {
        RegistrationState state = registrationState.get(chatId);
        state.setLastMessageId(messageId);
    }

    public boolean isRegistering(Long chatId) {
        return registrationState.containsKey(chatId);
    }

    public BotUserDTO getDTO(Long chatId) {
        return !registrationState.containsKey(chatId) ? null : registrationState.get(chatId).getUserDTO();
    }

    public Integer clearRegistrationState(Long chatId) {
        if (!registrationState.containsKey(chatId))
            return null;
        Integer lastMessageId = registrationState.get(chatId).getLastMessageId();
        registrationState.remove(chatId);

        return lastMessageId;
    }

    public void setRegistrationStep(Long chatId, RegistrationStep step) {
        RegistrationState state = registrationState.get(chatId);
        state.setStep(step);
    }

    public RegistrationStep getRegistrationStep(Long chatId) {
        RegistrationState state = registrationState.get(chatId);
        return state.getStep();
    }

    public void removeRegistrationStep(Long chatId) {
        registrationState.get(chatId).setStep(null);
    }

    @SneakyThrows
    public void processRegistrationStep(Long chatId, String text, String lang) {
        RegistrationState state = registrationState.get(chatId);
        RegistrationStep step = state.getStep();

        switch (step) {
            case NAME -> {
                Integer lastMessageId = setName(chatId, text);
                removeRegistrationStep(chatId);

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));
                Integer newMessageId = getRegisterState(chatId, lang);
                addMessageId(chatId, newMessageId);
            }
            case SURNAME -> {
                Integer lastMessageId = setSurname(chatId, text);
                removeRegistrationStep(chatId);

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));
                Integer newMessageId = getRegisterState(chatId, lang);
                addMessageId(chatId, newMessageId);
            }
            case PHONE -> {
                Integer lastMessageId = setPhone(chatId, text);
                removeRegistrationStep(chatId);

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));
                Integer newMessageId = getRegisterState(chatId, lang);
                addMessageId(chatId, newMessageId);
            }
            case FACULTY -> {
                Integer lastMessageId = setFaculty(chatId, text);
                removeRegistrationStep(chatId);

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));
                Integer newMessageId = getRegisterState(chatId, lang);
                addMessageId(chatId, newMessageId);
            }
            case COURSE -> {
                Integer lastMessageId = setCourse(chatId, text);
                removeRegistrationStep(chatId);

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));
                Integer newMessageId = getRegisterState(chatId, lang);
                addMessageId(chatId, newMessageId);
            }
            case GROUP -> {
                Integer lastMessageId = setGroup(chatId, text);
                removeRegistrationStep(chatId);

                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), lastMessageId));
                Integer newMessageId = getRegisterState(chatId, lang);
                addMessageId(chatId, newMessageId);
            }
            default -> throw new IllegalStateException("Unexpected value: " + step);
        }
    }

    @SneakyThrows
    public void checkHaveRegistrationState(Long chatId) {
        RegistrationState state = registrationState.get(chatId);

        if (state != null) {
            Integer messageId = state.getLastMessageId();

            if (messageId != null)
                executeUtil.execute(MessageUtil.deleteMessage(chatId.toString(), messageId));

            state.setLastMessageId(null);
            state.setUserDTO(new BotUserDTO());
        } else
            createRegistrationState(chatId);
    }

    public boolean saveRegistration(Long chatId) {
        BotUserDTO userDTO = registrationState.get(chatId).getUserDTO();

        if (!isRegistrationComplete(userDTO))
            return false;

        userDTO.setChatId(chatId);

        return true;
    }

    private Integer setName(Long chatId, String name) {
        RegistrationState state = registrationState.get(chatId);
        state.getUserDTO().setName(name);
        return state.getLastMessageId();
    }

    private Integer setSurname(Long chatId, String surname) {
        RegistrationState state = registrationState.get(chatId);
        state.getUserDTO().setSurname(surname);
        return state.getLastMessageId();
    }

    private Integer setPhone(Long chatId, String phone) {
        RegistrationState state = registrationState.get(chatId);
        state.getUserDTO().setPhone(phone);
        return state.getLastMessageId();
    }

    private Integer setFaculty(Long chatId, String faculty) {
        RegistrationState state = registrationState.get(chatId);
        state.getUserDTO().setFaculty(faculty);
        return state.getLastMessageId();
    }

    private Integer setCourse(Long chatId, String course) {
        RegistrationState state = registrationState.get(chatId);
        state.getUserDTO().setCourse(course);
        return state.getLastMessageId();
    }

    private Integer setGroup(Long chatId, String group) {
        RegistrationState state = registrationState.get(chatId);
        state.getUserDTO().setGroup(group);
        return state.getLastMessageId();
    }

    @SneakyThrows
    private Integer getRegisterState(Long chatId, String lang) {
        RegistrationState state = registrationState.get(chatId);
        BotUserDTO userDTO = state.getUserDTO();

        String name = safe(userDTO.getName());
        String surname = safe(userDTO.getSurname());
        String phone = safe(userDTO.getPhone());
        String faculty = safe(userDTO.getFaculty());
        String course = safe(userDTO.getCourse());
        String group = safe(userDTO.getGroup());

        String template = MessageUtil.get(MessageKeys.REGISTER_MESSAGE, lang);
        String registerState = String.format(template, name, surname, phone, faculty, course, group);

        SendMessage sendMessage = MessageUtil.createMessage(chatId.toString(), registerState);

        KeyboardUtil.getRegisterInlineKeyboard(sendMessage, lang);

        return executeUtil.execute(sendMessage).getMessageId();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private boolean isRegistrationComplete(BotUserDTO userDTO) {
        return userDTO.getName() != null && !userDTO.getName().isBlank() &&
                userDTO.getSurname() != null && !userDTO.getSurname().isBlank() &&
                userDTO.getPhone() != null && !userDTO.getPhone().isBlank() &&
                userDTO.getFaculty() != null && !userDTO.getFaculty().isBlank() &&
                userDTO.getCourse() != null && !userDTO.getCourse().isBlank() &&
                userDTO.getGroup() != null && !userDTO.getGroup().isBlank();
    }

}
