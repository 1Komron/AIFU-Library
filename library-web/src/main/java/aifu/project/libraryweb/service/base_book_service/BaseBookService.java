package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.excel_dto.BookExcelDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.live_dto.BaseBookUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BaseBookService {
    ResponseEntity<ResponseMessage> create(BaseBookCreateDTO dto);

    ResponseEntity<ResponseMessage> get(Integer id);

    ResponseEntity<ResponseMessage> update(Integer id, BaseBookUpdateDTO updates);

    ResponseEntity<ResponseMessage> delete(Integer id);

    long countBooks();

    ResponseEntity<ResponseMessage> getAll(String query, String field, int pageNumber, int pageSize, String sortDirection);

    List<BookExcelDTO> getAllBooks();

    ResponseEntity<ResponseMessage> importFromExcel(MultipartFile file);

    ResponseEntity<byte[]> templateFromExcel();
}