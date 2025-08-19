package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.exceptions.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.mapper.CategoryMapper;
import aifu.project.libraryweb.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    public ResponseEntity<ResponseMessage> create(CreateCategoryDTO dto) {
        Category existing = categoryRepository.findByName(dto.getName());
        if (existing != null) {
            log.error("Category '{}' already exists", dto.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(false, "Category already exists", null));
        }
        Category category = CategoryMapper.toEntity(dto);
        category = categoryRepository.save(category);

        log.info("New category created: {}", category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Category created successfully", CategoryMapper.toDto(category)));
    }


    @Override
    public ResponseEntity<ResponseMessage> update(Integer id, UpdateCategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.valueOf(id)));

        boolean exists = categoryRepository.existsByName(dto.getName());
        if (exists && !category.getName().equals(dto.getName())) {
            log.error("Category '{}' already exists", dto.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(false, "Category already exists", null));
        }

        category.setName(dto.getName());
        category = categoryRepository.save(category);

        log.info("Category updated: {}", category);

        return ResponseEntity.ok(new ResponseMessage(true, "Category updated", CategoryMapper.toDto(category)));
    }


    @Override
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.valueOf(id)));
        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            throw new CategoryNotFoundException("Cannot delete category with existing books");
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted: {}", category);
        return ResponseEntity.ok(new ResponseMessage(true, "Category deleted successfully", null));
    }


    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.valueOf(id)));
        log.info("Category retrieved: {}", category);
        return ResponseEntity.ok(new ResponseMessage(true, "Category found", CategoryMapper.toDto(category)));
    }


    @Override
    public ResponseEntity<ResponseMessage> getAll(String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<CategoryShortDTO> list = categoryRepository.findAllCategories(Sort.by(direction, "id"));

        log.info("Category list retrieved: {} entries", list.size());

        return ResponseEntity.ok(new ResponseMessage(true, "Category list", list));
    }


    @Override
    public Category getById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Override
    public List<Category> getHomePageCategories() {
        return categoryRepository.findRandomCategoriesWithBooks(5);
    }
}
