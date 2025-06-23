package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.mapper.CategoryMapper;
import aifu.project.libraryweb.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDTO create(CreateCategoryDTO createCategoryDTO) {
        Category category = CategoryMapper.toEntity(createCategoryDTO);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryResponseDTO update(Integer id, UpdateCategoryDTO updateCategoryDTO) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        CategoryMapper.updateEntity(updateCategoryDTO, existing);
        Category updated = categoryRepository.save(existing);
        return CategoryMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            throw new IllegalStateException("Categoryni o‘chirish mumkin emas, unga tegishli kitoblar mavjud");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
    }

    @Override
    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
