package aifu.project.libraryweb.service.base_book_service;

import aifu.project.common_domain.dto.CreateCategoryRequest;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.UpdateCategoryRequest;
import org.springframework.http.ResponseEntity;

public interface BaseBookCategoryService {
    ResponseEntity<ResponseMessage> create(CreateCategoryRequest request);

    ResponseEntity<ResponseMessage> update(Integer id, UpdateCategoryRequest request);

    ResponseEntity<ResponseMessage> delete(Integer id);

    ResponseEntity<ResponseMessage> getList();

    ResponseEntity<ResponseMessage> get(Integer id);
}
