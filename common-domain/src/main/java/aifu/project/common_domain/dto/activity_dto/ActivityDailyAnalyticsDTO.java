package aifu.project.common_domain.dto.activity_dto;

import java.time.LocalDate;

public record ActivityDailyAnalyticsDTO(
        LocalDate day,
        Long total,
        Long issued,
        Long extended,
        Long returned
) {
}

