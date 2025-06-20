package aifu.project.common_domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public static final String NOT_FOUND_BY_CHAT_ID = "User not found by chatId: ";
    public UserNotFoundException(String message) {
        super(message);
    }
}
