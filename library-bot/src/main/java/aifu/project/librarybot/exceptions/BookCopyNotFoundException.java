package aifu.project.librarybot.exceptions;

public class BookCopyNotFoundException extends RuntimeException {
    public BookCopyNotFoundException(String inventoryNumber) {
        super("Book copy not found by inventory number: " + inventoryNumber);
    }
}
