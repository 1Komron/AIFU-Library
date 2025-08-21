package aifu.project.libraryweb.service.history_service;


import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.History;
import aifu.project.common_domain.entity.Librarian;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface HistoryService {
    void add(Booking booking, Librarian librarian);

    ResponseEntity<ResponseMessage> getById(Long id);

    ResponseEntity<ResponseMessage> getAll(String field, String query, int pageNumber, int pageSize, String sortDirection);

    List<History> getAll();
}
