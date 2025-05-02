package aifu.project.libraryweb.service;

import aifu.project.commondomain.entity.Category;
import aifu.project.libraryweb.dto.CategoryDTO;
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
}
