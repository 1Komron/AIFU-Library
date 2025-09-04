package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.CreateStudentDTO;
import aifu.project.common_domain.entity.Student;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface StudentService {
    ResponseEntity<ResponseMessage> getAll(String filter, String query, String status, int pageNumber, int size, String sortDirection);

    ResponseEntity<ResponseMessage> getStudent(Long id);

    ResponseEntity<ResponseMessage> deleteStudent(Long id);

    ResponseEntity<ResponseMessage> getStudentByCardNumber(String id);

    ResponseEntity<ResponseMessage> update(Long id, Map<String, Object> updates);

    Student findStudent(Long id);

    List<Student> getAll();

    ResponseEntity<ResponseMessage> createStudent(CreateStudentDTO createStudentDTO);
}
