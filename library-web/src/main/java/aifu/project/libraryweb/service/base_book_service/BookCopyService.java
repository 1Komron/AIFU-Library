package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.payload.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BookCopyService {
    ResponseEntity<ResponseMessage> create(BookCopyCreateDTO dto);

    ResponseEntity<ResponseMessage> update(Integer id, Map<String,Object> updates);

    ResponseEntity<ResponseMessage> getAll(int pageNumber, int pageSize);

    ResponseEntity<ResponseMessage> getOne(Integer id);

    ResponseEntity<ResponseMessage> getAllByBaseBook(Integer baseBookId, int pageNumber, int pageSize);

    ResponseEntity<ResponseMessage> delete(Integer id);

    ResponseEntity<ResponseMessage> deleteByBaseBook(Integer baseBookId);

    Map<String,Long> getTotalAndTakenCount(Integer baseBookId);

    long count();

    Map<Integer, BookCopyStats> getStatsMap(List<Integer> bookIds);

    BookCopy findByEpc(String epc);

    void updateStatus(BookCopy bookCopy);
}