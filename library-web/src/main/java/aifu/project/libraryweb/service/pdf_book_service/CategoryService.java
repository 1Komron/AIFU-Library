package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {

    CategoryResponseDTO create(CreateCategoryDTO createCategoryDTO);

    CategoryResponseDTO update(Integer id, UpdateCategoryDTO updateCategoryDTO);

    void delete(Integer id);

    ResponseEntity<ResponseMessage> get(Integer id);

    Category getById(Integer id);

    ResponseEntity<ResponseMessage> getAll(String sortDirection);
}
