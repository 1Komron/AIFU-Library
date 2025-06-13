package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.mapper.CategoryMapper;
import aifu.project.common_domain.mapper.PdfBookMapper;
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
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        CategoryMapper.updateEntity(updateCategoryDTO, existing);
        Category updated = categoryRepository.save(existing);
        return CategoryMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {

        Category category = getEntityById(id);

        List<PdfBookPreviewDTO> books = getBooksByCategoryId(id);
         if(!books.isEmpty()) {
             throw new IllegalStateException("Cannot delete category with ID "
                     + id + " because it has associated books.");         }
         categoryRepository.delete(category);

    }

    @Override
    public CategoryResponseDTO getById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Category getEntityById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }

    @Override
    public List<PdfBookPreviewDTO> getBooksByCategoryId(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        return category.getBooks()
                .stream()
                .map(PdfBookMapper::toPreviewDto)
                .collect(Collectors.toList());
    }
}