package aifu.project.common_domain.payload;

public record UserSummaryDTO(
        Long id,
        String name,
        String surname,
        String phone,
        String faculty,
        String course,
        String group,
        Long chatId,
        boolean isActive
) {
}
