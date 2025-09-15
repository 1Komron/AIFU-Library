package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.pdf_book_dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service

public interface PdfBookService {

    PdfBookResponseDTO create(PdfBookCreateDTO dto);

    Map<String, Object> getList(int pageNumber, int pageSize, Integer category);

    PdfBookResponseDTO getOne(Integer id);

    PdfBookResponseDTO update(Integer id, PdfBookUpdateDTO updates);

    void delete(Integer id);

    byte[] downloadPdf(Integer id);

    Map<String, Object> getAll(PdfBookSearchCriteriaDTO criteria);

    ResponseEntity<ResponseMessage> getNewBooks();

    ResponseEntity<ResponseMessage> showByCategories();

    List<PdfBookShortDTO> getBooks(List<Long> list);
}