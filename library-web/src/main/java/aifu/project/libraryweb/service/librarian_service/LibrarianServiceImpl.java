package aifu.project.libraryweb.service.librarian_service;

import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LibrarianServiceImpl implements LibrarianService {
    @Override
    public ResponseEntity<ResponseMessage> profile() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Map<String, Object> updates) {
        return null;
    }
}
