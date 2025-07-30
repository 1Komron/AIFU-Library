package aifu.project.common_domain.dto.student_dto;

import aifu.project.common_domain.entity.Student;

public record StudentDTO(
        Long id,
        String name,
        String surname,
        String faculty,
        String degree,
        String cardNumber
) {
    public static StudentDTO toDTO(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getSurname(),
                student.getFaculty(),
                student.getDegree(),
                student.getCardNumber()
        );
    }
}
