package aifu.project.common_domain.exceptions;

public class HistoryNotFoundException extends RuntimeException {
    public HistoryNotFoundException(String message) {
        super(message);
    }
}
