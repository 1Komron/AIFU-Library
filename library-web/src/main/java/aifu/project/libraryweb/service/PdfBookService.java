package aifu.project.libraryweb.service;

import aifu.project.libraryweb.dto.PdfBookDTO;
import java.util.List;

public interface PdfBookService {
    PdfBookDTO create(Integer categoryId, PdfBookDTO dto);
    List<PdfBookDTO> getAllByCategory(Integer categoryId);
    PdfBookDTO getOne(Integer id);
    PdfBookDTO update(Integer id, PdfBookDTO dto);
    void delete(Integer id);
}
