package aifu.project.common_domain.mapper;

import aifu.project.common_domain.entity.Student;
import aifu.project.common_domain.payload.StudentDTO;

public class UserMapper {

    public static StudentDTO toBotDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setName(student.getName());
        dto.setSurname(student.getSurname());
        dto.setFaculty(student.getFaculty());
        dto.setDegree(student.getDegree());
        dto.setCardNumber(student.getCardNumber());
        dto.setChatId(student.getChatId());
        return dto;
    }




    private UserMapper() {}

}
