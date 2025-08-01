package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.live_dto.BaseBookCategoryDTO;
import aifu.project.common_domain.dto.CreateCategoryRequest;
import aifu.project.common_domain.dto.UpdateCategoryRequest;
import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.exceptions.BaseBookCategoryNotFoundException;
import aifu.project.common_domain.exceptions.CategoryDeletionException;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.repository.BaseBookCategoryRepository;
import aifu.project.libraryweb.repository.BaseBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseBookCategoryServiceImpl implements BaseBookCategoryService {
    private final BaseBookCategoryRepository categoryRepository;
    private final BaseBookRepository baseBookRepository;

    @Override
    public ResponseEntity<ResponseMessage> create(CreateCategoryRequest request) {
        String name = request.name();

        boolean exists = categoryRepository.existsByName(name);
        if (exists) {
            log.warn("BaseBookCategory with name '{}' already exists", name);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(false, "Category with this name already exists", null));
        }

        BaseBookCategory category = new BaseBookCategory();
        category.setName(name);
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = new BaseBookCategoryDTO(category.getId(), category.getName());

        log.info("BaseBookCategory create: {}", category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Category created", dto));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Integer id, UpdateCategoryRequest request) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        category.setName(request.name());
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = new BaseBookCategoryDTO(category.getId(), category.getName());

        log.info("BaseBookCategory name update: {}", category);

        return ResponseEntity.ok(new ResponseMessage(true, "Category updated", dto));
    }

    @Override
    public ResponseEntity<ResponseMessage> delete(Integer id) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        List<BaseBook> baseBookList = baseBookRepository.findByCategory_IdAndIsDeletedFalse(id);

        boolean allBooksDeleted = baseBookList.stream()
                .allMatch(BaseBook::isDeleted);

        if (!allBooksDeleted)
            throw new CategoryDeletionException("The category cannot be deleted: not all books in it have been deleted.");

        category.setDeleted(true);
        categoryRepository.save(category);

        log.info("BaseBookCategory deleted: {}", category);

        return ResponseEntity.ok(new ResponseMessage(true, "Category deleted", id));
    }

    @Override
    public ResponseEntity<ResponseMessage> getList(String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<BaseBookCategoryDTO> list = categoryRepository.findAllByIsDeletedFalse(Sort.by(direction, "id"));
        return ResponseEntity.ok(new ResponseMessage(true, "All categories", list));
    }

    @Override
    public ResponseEntity<ResponseMessage> get(Integer id) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        BaseBookCategoryDTO dto = new BaseBookCategoryDTO(category.getId(), category.getName());
        return ResponseEntity.ok(new ResponseMessage(true, "Category", dto));
    }
}