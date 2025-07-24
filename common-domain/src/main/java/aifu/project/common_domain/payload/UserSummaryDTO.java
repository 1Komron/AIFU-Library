package aifu.project.common_domain.payload;

public record UserSummaryDTO(
        Long id,
        String name,
        String surname,
        String degree,
        String faculty,
        Long chatId,
        boolean isActive
) {
}
