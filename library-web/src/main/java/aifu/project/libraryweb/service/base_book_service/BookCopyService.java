package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BookCopyService {
    ResponseEntity<ResponseMessage> create(BookCopyCreateDTO dto);

    ResponseEntity<ResponseMessage> update(Integer id, Map<String,Object> updates);

    ResponseEntity<ResponseMessage> getAll(int pageNumber, int pageSize);

    ResponseEntity<ResponseMessage> getOne(Integer id);

    ResponseEntity<ResponseMessage> getAllByBaseBook(Integer baseBookId, int pageNumber, int pageSize);

    ResponseEntity<ResponseMessage> delete(Integer id);

    long count();
}