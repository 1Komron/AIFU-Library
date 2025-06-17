package aifu.project.common_domain.exceptions;

public class BaseBookNotFoundException extends RuntimeException {
    public BaseBookNotFoundException(String message) {
        super(message);
    }
}
