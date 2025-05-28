package aifu.project.commondomain.mapper;

import aifu.project.commondomain.dto.live_dto.BaseBookDTO;
import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.Category;

import java.util.ArrayList;

public class BaseBookMapper {
    public static BaseBookDTO toDto(BaseBook entity) {
        if (entity == null) return null;
        return BaseBookDTO.builder()
                .id(entity.getId())
                .author(entity.getAuthor())
                .title(entity.getTitle())
                .series(entity.getSeries())
                .titleDetails(entity.getTitleDetails())
                .publicationYear(entity.getPublicationYear())
                .publisher(entity.getPublisher())
                .publicationCity(entity.getPublicationCity())
                .isbn(entity.getIsbn())
                .pageCount(entity.getPageCount())
                .language(entity.getLanguage())
                .price(entity.getPrice())
                .udc(entity.getUdc())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .copies(entity.getCopies() != null
                        ? entity.getCopies().stream().map(BookCopyMapper::toDto).toList()
                        : new ArrayList<>())
                .build();
    }

    public static BaseBook toEntity(BaseBookDTO dto, Category category) {
        if (dto == null) return null;
        return BaseBook.builder()
                .id(dto.getId())
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .series(dto.getSeries())
                .titleDetails(dto.getTitleDetails())
                .publicationYear(dto.getPublicationYear())
                .publisher(dto.getPublisher())
                .publicationCity(dto.getPublicationCity())
                .isbn(dto.getIsbn())
                .pageCount(dto.getPageCount())
                .language(dto.getLanguage())
                .price(dto.getPrice())
                .udc(dto.getUdc())
                .category(category)
                .build();
    }
}
