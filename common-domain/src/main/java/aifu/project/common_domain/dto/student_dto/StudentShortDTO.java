package aifu.project.common_domain.dto.student_dto;

public record StudentShortDTO(
        Long id,
        String name,
        String surname,
        String cardNumber,
        String degree,
        boolean status
) {
}
