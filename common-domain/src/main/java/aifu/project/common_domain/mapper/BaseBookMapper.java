package aifu.project.common_domain.mapper;

import aifu.project.common_domain.dto.live_dto.BaseBookDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;

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

    public static BaseBook toEntity(BaseBookDTO dto, BaseBookCategory category) {
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
