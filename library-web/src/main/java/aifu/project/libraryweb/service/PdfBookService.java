package aifu.project.libraryweb.service;

import aifu.project.commondomain.dto.PdfBookDTO;

import org.springframework.stereotype.Service;


import java.util.List;

@Service

public interface PdfBookService {
    PdfBookDTO create(Integer categoryId, PdfBookDTO dto);
    List<PdfBookDTO> getAllByCategory(Integer categoryId);
    PdfBookDTO getOne(Integer id);
    PdfBookDTO update(Integer id, PdfBookDTO dto);
    void delete(Integer id);
    byte[] downloadPdf(Integer id);
}