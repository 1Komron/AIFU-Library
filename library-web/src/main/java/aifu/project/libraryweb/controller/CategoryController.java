package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.mapper.CategoryMapper;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody CreateCategoryDTO dto) {
        CategoryResponseDTO response = categoryService.create(dto);
        ResponseMessage body = new ResponseMessage(
                true,
                "Category successfully created",
                response
        );
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCategoryDTO dto) {
        try {
            CategoryResponseDTO response = categoryService.update(id, dto);
            ResponseMessage body = new ResponseMessage(
                    true,
                    "Category successfully updated",
                    response
            );
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(404)
                    .body(new ResponseMessage(false, "Category not found", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok(
                    new ResponseMessage(true, "Category successfully deleted", null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(404)
                    .body(new ResponseMessage(false, "Category not found", null));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(400)
                    .body(new ResponseMessage(false, "Cannot delete category with existing books", null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAll() {
        List<CategoryResponseDTO> categories = categoryService.getAll();
        ResponseMessage body = new ResponseMessage(
                true,
                "Categories retrieved successfully",
                categories
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getOne(@PathVariable Integer id) {
        try {
            Category category = categoryService.getById(id);
            CategoryResponseDTO dto = CategoryMapper.toDto(category);
            return ResponseEntity.ok(
                    new ResponseMessage(true,
                            "Category successfully retrieved",
                            dto)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(404)
                    .body(new ResponseMessage(false,
                            "Category not found",
                            null));
        }

    }

    @GetMapping("/search")
    public ResponseEntity<ResponseMessage> search(@RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) Integer id,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(defaultValue = "id") String sortBy,
                                                  @RequestParam(defaultValue = "asc") String sortDir) {

        CategorySearchCriteriaDTO criteria = CategorySearchCriteriaDTO.builder()
                .id(id)
                .name(name)
                .pageNumber(pageNumber)
                .size(size)
                .sortBy(sortBy)
                .sortDr(sortDir)
                .build();

        Page<CategoryResponseDTO> result = categoryService.search(criteria);
        return ResponseEntity.ok(
                new ResponseMessage(true, "Search successful", result)
        );

    }

}
