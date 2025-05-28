package aifu.project.libraryweb.service.pdf_book_Service;

import aifu.project.commondomain.dto.pdf_book_dto.PdfBookCreateDTO;

import aifu.project.commondomain.dto.pdf_book_dto.PdfBookResponseDTO;
import aifu.project.commondomain.dto.pdf_book_dto.PdfBookUpdateDTO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service

public interface PdfBookService {

    PdfBookResponseDTO create(Integer categoryId, PdfBookCreateDTO dto);

    List<PdfBookResponseDTO> getAll();

    PdfBookResponseDTO getOne(Integer id);

    PdfBookResponseDTO update(Integer id, PdfBookUpdateDTO dto);

    void delete(Integer id);

    byte[] downloadPdf(Integer id);

}