package aifu.project.common_domain.dto.statistic_dto;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;

public record TopBookDTO(
        String title,
        String author,
        BaseBookCategoryDTO category,
        String isbn,
        int usageCount
) {
}
