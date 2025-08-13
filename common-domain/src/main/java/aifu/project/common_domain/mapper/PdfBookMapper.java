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

    /**     PATCH
     * UpdateDTO → Entity (mavjud obyektni yangilash)
     */
    public static void updateEntity(PdfBookUpdateDTO dto, PdfBook entity) {

        if (dto.getAuthor() != null) {
            entity.setAuthor(dto.getAuthor());
        }
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getPublicationYear() != null) {
            entity.setPublicationYear(dto.getPublicationYear());
        }
        if (dto.getSize() != null) {
            entity.setSize(dto.getSize());
        }
        if (dto.getPdfUrl() != null) {
            entity.setPdfUrl(dto.getPdfUrl());
        }
        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }
        if (dto.getScript() != null) {
            entity.setScript(dto.getScript());
        }
        if (dto.getLanguage() != null) {
            entity.setLanguage(dto.getLanguage());
        }
        if (dto.getPublisher() != null) {
            entity.setPublisher(dto.getPublisher());
        }
        if (dto.getPageCount() != null) {
            entity.setPageCount(dto.getPageCount());
        }
        if (dto.getIsbn() != null) {
            entity.setIsbn(dto.getIsbn());
        }
        if(dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getCategoryId() != null) {
            entity.setCategory(Category.builder().id(dto.getCategoryId()).build());
        }


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
 }
