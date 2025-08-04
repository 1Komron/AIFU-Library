package aifu.project.common_domain.dto.live_dto;

import aifu.project.common_domain.entity.BaseBookCategory;

public record BaseBookCategoryDTO(Integer id, String name) {
    public static BaseBookCategoryDTO toDTO(BaseBookCategory category) {
        return new BaseBookCategoryDTO(category.getId(), category.getName());
    }
}
