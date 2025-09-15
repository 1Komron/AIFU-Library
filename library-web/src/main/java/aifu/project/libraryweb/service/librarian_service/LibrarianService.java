package aifu.project.libraryweb.service.librarian_service;

import aifu.project.common_domain.dto.AdminUpdateDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface LibrarianService {
    ResponseEntity<ResponseMessage> profile();

    ResponseEntity<ResponseMessage> update(AdminUpdateDTO updates);
}
