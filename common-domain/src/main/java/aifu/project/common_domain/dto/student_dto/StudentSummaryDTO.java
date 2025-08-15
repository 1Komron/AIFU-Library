package aifu.project.common_domain.dto.student_dto;

import aifu.project.common_domain.entity.Student;

import java.time.LocalDate;

public record StudentSummaryDTO(
        Long id,
        String name,
        String surname,
        String degree,
        String faculty,
        LocalDate admissionTime,
        LocalDate graduationTime,
        String cardNumber,
        String phoneNumber,
        boolean isActive
) {
    public static StudentSummaryDTO toDTO(Student user) {
        return new StudentSummaryDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getDegree(),
                user.getFaculty(),
                user.getAdmissionTime(),
                user.getGraduationTime(),
                user.getCardNumber(),
                user.getPhoneNumber(),
                user.isActive()
        );
    }
}
