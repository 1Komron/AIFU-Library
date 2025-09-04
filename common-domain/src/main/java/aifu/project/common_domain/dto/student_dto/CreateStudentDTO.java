package aifu.project.common_domain.dto.student_dto;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateStudentDTO(
        @NotBlank String name,

        @NotBlank String surname,

        String phoneNumber,

        @NotBlank String faculty,

        @NotBlank String degree,

        @NotBlank String passportSeries,

        @NotBlank String passportNumber,

        @NotBlank String cardNumber,

        @JsonFormat(pattern = "yyyy")
        @NotNull Integer admissionTime,

        @JsonFormat(pattern = "yyyy")
        @NotNull Integer graduationTime
) {
    public static Student toEntity(CreateStudentDTO createStudentDTO, String passport, String cardNumber) {
        Student student = new Student();
        student.setChatId(null);
        student.setActive(false);
        student.setDeleted(false);
        student.setRole(Role.STUDENT);
        student.setName(createStudentDTO.name());
        student.setSurname(createStudentDTO.surname());
        student.setPhoneNumber(createStudentDTO.phoneNumber());
        student.setFaculty(createStudentDTO.faculty());
        student.setDegree(createStudentDTO.degree());
        student.setPassportCode(passport);
        student.setCardNumber(cardNumber);
        student.setAdmissionTime(LocalDate.of(createStudentDTO.admissionTime(), 8, 1));
        student.setGraduationTime(LocalDate.of(createStudentDTO.graduationTime(), 7, 1));

        return student;
    }
}
