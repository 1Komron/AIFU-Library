package aifu.project.common_domain.exceptions;

public class NotificationNotFoundException extends RuntimeException {
    public static final String NOTIFICATION_NOT_FOUND = "Notification not found by id: ";

    public NotificationNotFoundException(String message) {
        super(message);
    }
}
