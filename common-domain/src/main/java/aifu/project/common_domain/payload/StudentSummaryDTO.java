package aifu.project.common_domain.payload;

public record StudentSummaryDTO(
        Long id,
        String name,
        String surname,
        String phone,
        String faculty,
        Long chatId,
        boolean isActive
) {
}
