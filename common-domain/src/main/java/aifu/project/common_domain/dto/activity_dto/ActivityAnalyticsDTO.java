package aifu.project.common_domain.dto.activity_dto;

public record ActivityAnalyticsDTO(
        Long totalCount,
        Long issuedCount,
        Long extendedCount,
        Long returnedCount
) {
}
