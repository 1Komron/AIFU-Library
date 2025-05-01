package aifu.project.libraryweb.service;

import aifu.project.commondomain.entity.Category;
import aifu.project.commondomain.repository.CategoryRepository;
import aifu.project.libraryweb.dto.CategoryDTO;
import aifu.project.libraryweb.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        Category category = CategoryMapper.toEntity(categoryDTO);
        category.getBooks().forEach(book -> book.setCategory(category));
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDTO update(Integer id, CategoryDTO categoryDTO) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(categoryDTO.getName());
        Category updated = categoryRepository.save(existing);
        return CategoryMapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
     categoryRepository.deleteById(id);
    }


    @Override
    public CategoryDTO getById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Category getEntityById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }


}
