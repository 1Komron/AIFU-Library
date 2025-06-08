package aifu.project.libraryweb.service;

import aifu.project.commondomain.entity.BaseBookCategory;
import aifu.project.libraryweb.repository.BaseBookCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseBookCategoryService {
    private final BaseBookCategoryRepository categoryRepository;

    public BaseBookCategory getEntityById(Integer categoryId) {
        return categoryRepository.findById(categoryId);
    }
}
