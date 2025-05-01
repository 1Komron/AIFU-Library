package aifu.project.libraryweb.mapper;
import aifu.project.commondomain.entity.Category;
import aifu.project.libraryweb.dto.CategoryDTO;

public class CategoryMapper {

    /** Entity → DTO */
    public static CategoryDTO toDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        dto.setName(category.getName());
        return dto;
    }


    /** DTO → Entity */
    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
