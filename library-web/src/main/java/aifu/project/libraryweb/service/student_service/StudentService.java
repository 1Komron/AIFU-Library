package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.entity.Student;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StudentService {
    ResponseEntity<ResponseMessage> getAll(String filter, String query, String status, int pageNumber, int size, String sortDirection);

    ResponseEntity<ResponseMessage> getStudent(String id);

    ResponseEntity<ResponseMessage> deleteStudent(Long userId);

    ResponseEntity<ResponseMessage> getStudentByCardNumber(String id);

    ResponseEntity<ResponseMessage> updateCardNumber(Long id, String cardNumber);

    boolean existsStudent(Long id);

    List<Student> getAll();
}
