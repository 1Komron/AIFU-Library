package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;

public interface StudentService {
    ResponseEntity<ResponseMessage> getAll(String filter, String query, String status, int pageNumber, int size, String sortDirection);

    ResponseEntity<ResponseMessage> getStudent(String id);

    ResponseEntity<ResponseMessage> deleteStudent(Long userId);

}
