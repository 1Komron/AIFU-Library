package aifu.project.common_domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PdfFileDownloadException extends RuntimeException {
    public PdfFileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}