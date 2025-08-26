package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.BookCopyStats;
import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BookCopy;
import aifu.project.common_domain.dto.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BookCopyService {
    ResponseEntity<ResponseMessage> create(BookCopyCreateDTO dto);

    ResponseEntity<ResponseMessage> update(Integer id, Map<String, Object> updates);

    ResponseEntity<ResponseMessage> get(Integer id);

    ResponseEntity<ResponseMessage> getAll(String query, String field, int pageNumber, int pageSize, String sortDirection);

    ResponseEntity<ResponseMessage> delete(Integer id);

    Map<String, Long> getTotalAndTakenCount(Integer baseBookId);

    long count();

    Map<Integer, BookCopyStats> getStatsMap(List<Integer> bookIds);

    BookCopy findByEpc(String epc);

    BookCopy findByInventoryNumber(String inventoryNumber);

    void updateStatus(BookCopy bookCopy, boolean isTaken);

    ResponseEntity<ResponseMessage> checkInventoryNumber(String inventoryNumber);

    ResponseEntity<ResponseMessage> getByEPC(String epc);

    List<String> findByBaseBookId(Integer id);

    void saveBookCopies(BaseBook baseBook, List<String> strings);
}