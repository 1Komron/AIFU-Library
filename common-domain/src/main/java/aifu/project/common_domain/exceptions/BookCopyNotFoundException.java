package aifu.project.common_domain.exceptions;

public class BookCopyNotFoundException extends RuntimeException {

    public static final String BY_ID = "Kitob nusxasi topilmadi. ID: ";

    public BookCopyNotFoundException(String message) {
        super(message);
    }
}
