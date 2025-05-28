package aifu.project.commondomain.mapper;

import aifu.project.commondomain.dto.CategoryDTO;

import aifu.project.commondomain.entity.Category;

import java.util.ArrayList;

public class CategoryMapper {
    public static CategoryDTO toDto(Category entity) {
        if (entity == null) return null;
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();

    }
}