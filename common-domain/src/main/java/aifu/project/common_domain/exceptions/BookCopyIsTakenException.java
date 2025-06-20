package aifu.project.common_domain.exceptions;

public class BookCopyIsTakenException extends RuntimeException {
    public BookCopyIsTakenException(String message) {
        super(message);
    }
}
