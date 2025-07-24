package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.librarybot.enums.InputStep;
import aifu.project.librarybot.repository.StudentRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.KeyboardUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static aifu.project.common_domain.exceptions.UserNotFoundException.NOT_FOUND_BY_CHAT_ID;


@Service
@RequiredArgsConstructor
public class UserService {
    private final StudentRepository studentRepository;
    private final UserLanguageService userLanguageService;
    private final ExecuteUtil executeUtil;
    private final InputService inputService;

    public boolean exists(Long chatId) {
        return studentRepository.existsByChatIdAndIsActiveTrueAndIsDeletedFalse(chatId);
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
        inputService.putState(chatId, InputStep.LOGIN);
        executeUtil.executeMessage(chatId.toString(), MessageKeys.PASSPORT_REQUEST_MESSAGE, lang);
    }


    public void login(Long chatId, String text, String lang) {
        Student student = studentRepository.findByIsDeletedFalseAndPassportCode(text);
        if (student != null) {
            saveUserChatId(student, chatId);
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
        Student student = studentRepository.findByChatIdAndIsDeletedFalse(chatId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_BY_CHAT_ID + chatId));

        String template = MessageUtil.get(MessageKeys.PROFILE, lang);
        return String.format(template, student.getId(), student.getName(), student.getSurname(), student.getFaculty(), student.getDegree(), student.getChatId());
    }

    private void saveUserChatId(Student student, Long chatId) {
        student.setChatId(chatId);
        student.setActive(true);
        studentRepository.save(student);
    }
}
