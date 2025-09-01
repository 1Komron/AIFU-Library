package aifu.project.common_domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(UUID jobId, String reportType) {
        super(String.format("'%s' ID'li jarayon uchun '%s' turidagi hisobot topilmadi.", jobId, reportType));
    }
}