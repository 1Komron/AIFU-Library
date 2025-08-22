package aifu.project.common_domain.exceptions;

public class CardNumberAlreadyExistsException extends RuntimeException {
    public CardNumberAlreadyExistsException(String message) {
        super(message);
    }
}
