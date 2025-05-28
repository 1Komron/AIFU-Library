package aifu.project.commondomain.mapper;

import aifu.project.commondomain.dto.PdfBookDTO;
import aifu.project.commondomain.entity.Category;
import aifu.project.commondomain.entity.PdfBook;
import org.springframework.stereotype.Component;

@Component
public class PdfBookMapper {

    // Entity -> DTO
    public static PdfBookDTO toDto(PdfBook pdfBook) {
        if (pdfBook == null) return null;
        PdfBookDTO pdfBookDTO = new PdfBookDTO();
        pdfBookDTO.setId(pdfBook.getId());
        pdfBookDTO.setAuthor(pdfBook.getAuthor());
        pdfBookDTO.setPdfUrl(pdfBook.getPdfUrl());
        pdfBookDTO.setImageUrl(pdfBook.getImageUrl());
        pdfBookDTO.setTitle(pdfBook.getTitle());
        pdfBookDTO.setPublicationYear(pdfBook.getPublicationYear());
        pdfBookDTO.setLocalDate(pdfBook.getLocalDate());
        pdfBookDTO.setPageCount(pdfBook.getPageCount());
        pdfBookDTO.setLanguage(pdfBook.getLanguage());
        pdfBookDTO.setScript(pdfBook.getScript());
        pdfBookDTO.setPublisher(pdfBook.getPublisher());
        pdfBookDTO.setIsbn(pdfBook.getIsbn());
        pdfBookDTO.setSize(pdfBook.getSize());

        return pdfBookDTO;
    }

    // DTO -> Entity
    public PdfBook toEntity(PdfBookDTO pdfBookDTO, Category category) {
        if (pdfBookDTO == null) return null;
        PdfBook pdfBook = new PdfBook();
        pdfBook.setId(pdfBookDTO.getId());
        pdfBook.setAuthor(pdfBookDTO.getAuthor());
        pdfBook.setTitle(pdfBookDTO.getTitle());
        pdfBook.setPublicationYear(pdfBookDTO.getPublicationYear());
        pdfBook.setLocalDate(pdfBookDTO.getLocalDate());
        pdfBook.setPdfUrl(pdfBookDTO.getPdfUrl());
        pdfBook.setImageUrl(pdfBookDTO.getImageUrl());
        pdfBook.setScript(pdfBookDTO.getScript());
        pdfBook.setLanguage(pdfBookDTO.getLanguage());
        pdfBook.setPageCount(pdfBookDTO.getPageCount());
        pdfBook.setPublisher(pdfBookDTO.getPublisher());
        pdfBook.setIsbn(pdfBookDTO.getIsbn());
        return pdfBook;
    }

}
