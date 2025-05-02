package aifu.project.libraryweb.mapper;

import aifu.project.commondomain.entity.Category;
import aifu.project.commondomain.entity.PdfBook;
import aifu.project.libraryweb.dto.PdfBookDTO;

public class PdfBookMapper {

    /**
     * Entity -> DTO
     */
    public static PdfBookDTO toDto(PdfBook pdfBook) {
        if (pdfBook == null) return null;
        PdfBookDTO pdfBookDTO = new PdfBookDTO();
        pdfBookDTO.setAuthor(pdfBook.getAuthor());
        pdfBookDTO.setTitle(pdfBook.getTitle());
        pdfBookDTO.setPdfUrl(pdfBook.getPdfUrl());
        pdfBookDTO.setImageUrl(pdfBook.getImageUrl());
        pdfBookDTO.setPublicationYear(pdfBook.getPublicationYear());
        pdfBookDTO.setLocalDate(pdfBook.getLocalDate());
        return pdfBookDTO;

    }

    /**
     * DTO -> Entity
     */
    public static PdfBook toEntity(PdfBookDTO pdfBookDTO, Category category) {
        if (pdfBookDTO == null) return null;

        PdfBook pdfBook = new PdfBook();
        pdfBook.setAuthor(pdfBookDTO.getAuthor());
        pdfBookDTO.setTitle(pdfBookDTO.getTitle());
        pdfBook.setPublicationYear(pdfBookDTO.getPublicationYear());
        pdfBook.setLocalDate(pdfBookDTO.getLocalDate());
        pdfBook.setImageUrl(pdfBookDTO.getImageUrl());
        pdfBook.setPdfUrl(pdfBookDTO.getPdfUrl());
        pdfBook.setCategory(category);
        return pdfBook;
    }
}
