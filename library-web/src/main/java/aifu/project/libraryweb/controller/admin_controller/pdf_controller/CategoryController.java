package aifu.project.libraryweb.controller.admin_controller.pdf_controller;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.mapper.CategoryMapper;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
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
    @Operation(summary = "Barcha pdf category larni olish")
    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return categoryService.getAll(sortDirection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pdf kitoblar uchun category ID boyicha malumotini olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "404", description = "ID boyicha category topilmadi")
    })
    public ResponseEntity<ResponseMessage> getOne(@PathVariable Integer id) {
        return categoryService.get(id);

    }

}
