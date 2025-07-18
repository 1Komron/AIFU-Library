package aifu.project.common_domain.mapper;

import aifu.project.common_domain.dto.pdf_book_dto.CreateCategoryDTO;
import aifu.project.common_domain.dto.pdf_book_dto.CategoryResponseDTO;
import aifu.project.common_domain.dto.pdf_book_dto.UpdateCategoryDTO;
import aifu.project.common_domain.entity.Category;

public class CategoryMapper {

    // CreateCategoryDTO → Category
    public static Category toEntity(CreateCategoryDTO dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    // Category → CategoryResponseDTO
    public static CategoryResponseDTO toDto(Category entity) {
        return CategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    // UpdateCategoryDTO → Category (mavjud entityni yangilash)
    public static void updateEntity(UpdateCategoryDTO dto, Category entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }


}