package aifu.project.common_domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(UUID jobId, String reportType, Throwable cause) {
        super(String.format("'%s' ID'li jarayon uchun '%s' turidagi hisobotni generatsiya qilishda xatolik yuz berdi.", jobId, reportType), cause);
    }
}