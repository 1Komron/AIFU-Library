package aifu.project.common_domain.payload;

public record StudentSummaryDTO(
        Long id,
        String name,
        String surname,
        String degree,
        String faculty,
        String cardNumber,
        Long chatId,
        boolean isActive
) {
}
