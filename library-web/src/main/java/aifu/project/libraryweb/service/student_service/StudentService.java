package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;

public interface StudentService {
    ResponseEntity<ResponseMessage> getStudentList(String filter, int pageNumber, int size, String sortDirection);

    ResponseEntity<ResponseMessage> getSearchStudentList(String filter, String query, int pageNumber, int size, String sortDirection);

    ResponseEntity<ResponseMessage> getStudent(String id);

    ResponseEntity<ResponseMessage> deleteStudent(Long userId);

}
