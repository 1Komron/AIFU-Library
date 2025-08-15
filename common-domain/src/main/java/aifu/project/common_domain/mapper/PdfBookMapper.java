package aifu.project.common_domain.mapper;


import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.entity.PdfBook;

import java.time.LocalDate;

public class PdfBookMapper {


    // POST → Entity
    public static PdfBook toEntity(PdfBookCreateDTO dto) {
        return PdfBook.builder()
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .publicationYear(dto.getPublicationYear())
                .pdfUrl(dto.getPdfUrl())
                .imageUrl(dto.getImageUrl())
                .script(dto.getScript())
                .language(dto.getLanguage())
                .publisher(dto.getPublisher())
                .pageCount(dto.getPageCount())
                .isbn(dto.getIsbn())
                .size(dto.getSize())
                .localDate(LocalDate.now())
                .description(dto.getDescription())
                .build();
    }


    // Entity → Response DTO

    public static PdfBookResponseDTO toDto(PdfBook entity) {
        return PdfBookResponseDTO.builder()
                .id(entity.getId())
                .size(entity.getSize())
                .author(entity.getAuthor())
                .title(entity.getTitle())
                .publicationYear(entity.getPublicationYear())
                .pdfUrl(entity.getPdfUrl())
                .imageUrl(entity.getImageUrl())
                .isbn(entity.getIsbn())
                .pageCount(entity.getPageCount())
                .publisher(entity.getPublisher())
                .language(entity.getLanguage())
                .script(entity.getScript())
                .createdDate(entity.getLocalDate())
                .description(entity.getDescription())
                .categoryPreview(toCategoryPreviewDTO(entity.getCategory()))
                .build();

    }


    public static PdfBookPreviewDTO toPreviewDto(PdfBook entity) {
        return PdfBookPreviewDTO.builder()
                .id(entity.getId())
                .author(entity.getAuthor())
                .title(entity.getTitle())
                .imageUrl(entity.getImageUrl())
                .categoryPreviewDTO(toCategoryPreviewDTO(entity.getCategory()))
                .build();
    }

    private static CategoryPreviewDTO toCategoryPreviewDTO(Category category) {
        if (category == null) return null;

        return CategoryPreviewDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static PdfBookShortDTO toPdfBookShortDTO(PdfBook entity) {
        return PdfBookShortDTO.builder()
                .id(entity.getId())
                .isbn(entity.getIsbn())
                .author(entity.getAuthor())
                .title(entity.getTitle())
                .imageUrl(entity.getImageUrl())
                .categoryPreviewDTO(toCategoryPreviewDTO(entity.getCategory()))
                .build();
    }
 }
