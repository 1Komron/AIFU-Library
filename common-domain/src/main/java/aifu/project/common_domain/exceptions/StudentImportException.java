package aifu.project.common_domain.exceptions;

public class StudentImportException extends RuntimeException {
    public StudentImportException(String message) {
        super(message);
    }
    public StudentImportException(String message, Throwable cause) {
        super(message, cause);
    }
}