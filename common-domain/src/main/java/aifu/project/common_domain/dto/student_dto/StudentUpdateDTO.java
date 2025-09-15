package aifu.project.common_domain.dto.student_dto;

public record StudentUpdateDTO(
        String name,

        String surname,

        String phoneNumber,

        String faculty,

        String degree,

        String cardNumber,

        Integer admissionTime,

        Integer graduationTime
) {
}
