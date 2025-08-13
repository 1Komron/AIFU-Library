package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import org.springframework.http.ResponseEntity;


public interface CategoryService {

    ResponseEntity<ResponseMessage> create(CreateCategoryDTO dto);

    ResponseEntity<ResponseMessage> update(Integer id, UpdateCategoryDTO dto);

    ResponseEntity<ResponseMessage> delete(Integer id);

    ResponseEntity<ResponseMessage> get(Integer id);

    Category getById(Integer id);

    ResponseEntity<ResponseMessage> getAll(String sortDirection);
}
