package aifu.project.common_domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)

public class PdfBookNotFoundException extends RuntimeException {
    public PdfBookNotFoundException(String message) {
        super(message);
    }
}