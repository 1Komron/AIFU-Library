package aifu.project.common_domain.dto.student_dto;

import aifu.project.common_domain.entity.Student;

public record StudentSummaryDTO(
        Long id,
        String name,
        String surname,
        String degree,
        String faculty,
        String cardNumber,
        boolean isActive
) {
    public static StudentSummaryDTO toDTO(Student user) {
        return new StudentSummaryDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getDegree(),
                user.getFaculty(),
                user.getCardNumber(),
                user.isActive()
        );
    }
}
