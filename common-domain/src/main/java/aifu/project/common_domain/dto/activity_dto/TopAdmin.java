package aifu.project.common_domain.dto.activity_dto;

public record TopAdmin(
        Long id,
        String imageUrl,
        String name,
        String surname,
        String email,
        ActivityAnalyticsDTO analytics
) {
}
