package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BaseBookService {
    ResponseEntity<ResponseMessage> create(BaseBookCreateDTO dto);

    ResponseEntity<ResponseMessage> get(Integer id);

    ResponseEntity<ResponseMessage> update(Integer id, Map<String, Object> updates);

    ResponseEntity<ResponseMessage> delete(Integer id);

    long countBooks();

    ResponseEntity<ResponseMessage> getAll(String query, String field, int pageNumber, int pageSize, String sortDirection);
}