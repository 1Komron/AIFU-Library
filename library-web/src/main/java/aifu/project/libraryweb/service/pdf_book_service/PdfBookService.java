package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.*;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Service

public interface PdfBookService {

    PdfBookResponseDTO create(Integer categoryId, PdfBookCreateDTO dto);

    Map<String, Object> getList(int pageNumber, int pageSize);

    PdfBookResponseDTO getOne(Integer id);

    PdfBookResponseDTO update(Integer id, PdfBookUpdateDTO dto);

    void delete(Integer id);

    byte[] downloadPdf(Integer id);

    List<PdfBookPreviewDTO> getBooksByCategoryId(Integer categoryId);

    Page<PdfBookResponseDTO> search(PdfBookSearchCriteriaDTO criteria);

}