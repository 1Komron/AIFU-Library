package aifu.project.common_domain.dto.student_dto;

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
