package aifu.project.common_domain.dto.student_dto;

public record CreateStudentDTO(
        String name,
        String surname,
        String phoneNumber,
        String faculty,
        String degree,
        String passportSeries,
        String passportNumber,
        String cardNumber,
        Integer admissionTime,
        Integer graduationTime
) {
}
