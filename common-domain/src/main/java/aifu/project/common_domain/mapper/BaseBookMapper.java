package aifu.project.common_domain.mapper;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookResponseDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;

public class BaseBookMapper {

    public static BaseBook toEntity(BaseBookCreateDTO dto, BaseBookCategory category) {
        return BaseBook.builder()
                .author(dto.author().trim())
                .title(dto.title().trim())
                .series(dto.series() == null ? null : dto.series().trim())
                .titleDetails(dto.titleDetails() == null ? null : dto.titleDetails().trim())
                .publicationYear(dto.publicationYear())
                .publisher(dto.publisher().trim())
                .publicationCity(dto.publicationCity().trim())
                .isbn(dto.isbn().trim())
                .pageCount(dto.pageCount())
                .language(dto.language().trim())
                .udc(dto.udc().trim())
                .category(category)
                .build();
    }

    public static BaseBookResponseDTO toResponseDTO(BaseBook entity) {
        return new BaseBookResponseDTO(
                entity.getId(),
                entity.getAuthor(),
                entity.getTitle(),
                entity.getSeries(),
                entity.getTitleDetails(),
                entity.getPublicationYear(),
                entity.getPublisher(),
                entity.getPublicationCity(),
                entity.getIsbn(),
                entity.getPageCount(),
                entity.getLanguage(),
                entity.getUdc(),
                BaseBookCategoryDTO.toDTO(entity.getCategory())
        );
    }

    private BaseBookMapper() {
    }
}
