package aifu.project.commondomain.exceptions;

public class BookCopyNotFoundException extends RuntimeException {
    public BookCopyNotFoundException(String number) {
        super("Book copy not found by inventory number or id: " + number);
    }
}
