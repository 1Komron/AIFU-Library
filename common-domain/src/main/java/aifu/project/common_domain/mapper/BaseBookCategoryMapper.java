package aifu.project.common_domain.mapper;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryCreateDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookCategoryResponseDTO;
import aifu.project.common_domain.dto.pdf_book_dto.UpdateCategoryDTO;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.entity.Category;

public class BaseBookCategoryMapper {

    // CreateCategoryDTO → Category
    public static BaseBookCategory toEntity (BaseBookCategoryCreateDTO dto){
        return BaseBookCategory.builder()
                        .name(dto.getName())
                        .build();
    }

    // Category → CategoryResponseDTO
public static BaseBookCategoryResponseDTO toDto (BaseBookCategory entity){

        return BaseBookCategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
}
    // UpdateCategoryDTO → Category (mavjud entityni yangilash)
    public static void updateEntity(BaseBookCategoryCreateDTO dto, BaseBookCategory entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }

}
