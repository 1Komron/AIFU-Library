package aifu.project.common_domain.mapper;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookResponseDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookUpdateDTO;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;

public class BaseBookMapper {

    public static BaseBook toEntity(BaseBookCreateDTO dto, BaseBookCategory category) {
        return BaseBook.builder()
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
                .udc(dto.getUdc())
                .category(category)
                .build();
    }

    public static BaseBookResponseDTO toResponseDTO(BaseBook entity) {
        BaseBookResponseDTO dto = new BaseBookResponseDTO();
        dto.setId(entity.getId());
        dto.setAuthor(entity.getAuthor());
        dto.setTitle(entity.getTitle());
        dto.setSeries(entity.getSeries());
        dto.setTitleDetails(entity.getTitleDetails());
        dto.setPublicationYear(entity.getPublicationYear());
        dto.setPublisher(entity.getPublisher());
        dto.setPublicationCity(entity.getPublicationCity());
        dto.setIsbn(entity.getIsbn());
        dto.setPageCount(entity.getPageCount());
        dto.setLanguage(entity.getLanguage());
        dto.setUdc(entity.getUdc());
        dto.setCategory(BaseBookCategoryDTO.toDTO(entity.getCategory()));
        return dto;
    }

    public static void updateEntity(BaseBook entity, BaseBookUpdateDTO dto, BaseBookCategory category) {
        entity.setAuthor(dto.getAuthor());
        entity.setTitle(dto.getTitle());
        entity.setSeries(dto.getSeries());
        entity.setTitleDetails(dto.getTitleDetails());
        entity.setPublicationYear(dto.getPublicationYear());
        entity.setPublisher(dto.getPublisher());
        entity.setPublicationCity(dto.getPublicationCity());
        entity.setIsbn(dto.getIsbn());
        entity.setPageCount(dto.getPageCount());
        entity.setLanguage(dto.getLanguage());
        entity.setUdc(dto.getUdc());
        entity.setCategory(category);
    }

    private BaseBookMapper() {
    }
}
