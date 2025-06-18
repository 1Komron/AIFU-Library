package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.BaseBookCategoryDTO;
import aifu.project.common_domain.dto.CreateCategoryRequest;
import aifu.project.common_domain.dto.UpdateCategoryRequest;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.exceptions.BaseBookCategoryNotFoundException;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.repository.BaseBookCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseBookCategoryService {
    private final BaseBookCategoryRepository categoryRepository;

    public ResponseEntity<ResponseMessage> create(CreateCategoryRequest request) {
        BaseBookCategory category = new BaseBookCategory();
        category.setName(request.name());
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = new BaseBookCategoryDTO(category.getId(), category.getName());

        log.info("BaseBookCategory create: {}", category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(true, "Category created", dto));
    }

    public ResponseEntity<ResponseMessage> update(Integer id, UpdateCategoryRequest request) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        category.setName(request.name());
        category = categoryRepository.save(category);
        BaseBookCategoryDTO dto = new BaseBookCategoryDTO(category.getId(), category.getName());

        log.info("BaseBookCategory name update: {}", category);

        return ResponseEntity.ok(new ResponseMessage(true, "Category updated", dto));
    }

    public ResponseEntity<ResponseMessage> delete(Integer id) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        category.setDeleted(true);
        categoryRepository.save(category);

        log.info("BaseBookCategory deleted: {}", category);

        return ResponseEntity.ok(new ResponseMessage(true, "Category deleted", id));
    }

    public ResponseEntity<ResponseMessage> getList() {
        List<BaseBookCategoryDTO> list = categoryRepository.findAllByIsDeletedFalse();
        return ResponseEntity.ok(new ResponseMessage(true, "All categories", list));
    }

    public ResponseEntity<ResponseMessage> get(Integer id) {
        BaseBookCategory category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseBookCategoryNotFoundException(id));

        BaseBookCategoryDTO dto = new BaseBookCategoryDTO(category.getId(), category.getName());
        return ResponseEntity.ok(new ResponseMessage(true, "Category", dto));
    }
}