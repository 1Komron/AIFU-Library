package aifu.project.common_domain.dto.activity_dto;

import java.time.LocalDateTime;

public record ActivityDTO(
        String studentName,
        String studentSurname,
        String bookAuthor,
        String bookTitle,
        String bookInventoryNumber,
        String action,
        LocalDateTime time,
        ActivityAnalyticsDTO analytics
) {
}
