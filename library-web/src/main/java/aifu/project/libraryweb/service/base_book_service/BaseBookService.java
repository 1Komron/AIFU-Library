package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BaseBookService {
    ResponseEntity<ResponseMessage> create(BaseBookCreateDTO dto);

    ResponseEntity<ResponseMessage> getAll(int pageNumber, int pageSize);

    ResponseEntity<ResponseMessage> getOne(Integer id);

    ResponseEntity<ResponseMessage> update(Integer id, Map<String, Object> updates);

    ResponseEntity<ResponseMessage> delete(Integer id);

    ResponseEntity<ResponseMessage> deleteByCategory(Integer id);

    long countBooks();
}