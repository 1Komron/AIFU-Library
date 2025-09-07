package aifu.project.common_domain.exceptions;

import lombok.Getter;

import java.util.List;

public class BookImportNonValidHeaderException extends RuntimeException {
    @Getter
    private final List<String> errors;

    public BookImportNonValidHeaderException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
