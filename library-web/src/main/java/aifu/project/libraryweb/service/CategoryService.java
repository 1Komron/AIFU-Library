package aifu.project.libraryweb.service;

import aifu.project.commondomain.dto.CategoryDTO;
import aifu.project.commondomain.dto.PdfBookDTO;
import aifu.project.commondomain.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    CategoryDTO create(CategoryDTO categoryDTO);
    CategoryDTO update(Integer id, CategoryDTO categoryDTO);
    void delete(Integer id);
    CategoryDTO getById(Integer id);
    List<CategoryDTO> getAll();
    Category getEntityById(Integer categoryId);
    List<PdfBookDTO> getBooksByCategoryId(Integer categoryId);
}