package aifu.project.common_domain.exceptions;

public class BookCopyNotFoundException extends RuntimeException {

    public static final String BY_INVENTORY_NUMBER = "Book copy not found by inventory number or id: ";
    public static final String BY_ID = "Book copy not found by id: ";

    public BookCopyNotFoundException(String message) {
        super(message);
    }
}
