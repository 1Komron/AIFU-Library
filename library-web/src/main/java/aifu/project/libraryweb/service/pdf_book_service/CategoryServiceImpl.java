package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.exceptions.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.mapper.CategoryMapper;
import aifu.project.libraryweb.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Override
    public ResponseEntity<ResponseMessage> getAll(String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        List<CategoryShortDTO> categories = categoryRepository.findAllCategories(Sort.by(direction, "id"));

        log.info("categories: {}", categories);
        log.info("categories size: {}", categories.size());

        return ResponseEntity.ok(new ResponseMessage(true, "Categories retrieved successfully", categories));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        Category category = getById(id);

        log.info("Getting category with id: {} -> category {}", id, category);

        CategoryResponseDTO dto = CategoryMapper.toDto(category);
        return ResponseEntity.ok(new ResponseMessage(true, "Category successfully retrieved", dto));
    }

}
