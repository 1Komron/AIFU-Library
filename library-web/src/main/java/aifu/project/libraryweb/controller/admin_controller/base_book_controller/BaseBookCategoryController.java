package aifu.project.libraryweb.controller.admin_controller.base_book_controller;

import aifu.project.common_domain.dto.CreateCategoryRequest;
import aifu.project.common_domain.dto.UpdateCategoryRequest;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BaseBookCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/base-book/categories")
@RequiredArgsConstructor
public class BaseBookCategoryController {
    private final BaseBookCategoryService baseBookCategoryService;

    @PostMapping
    @Operation(summary = "Base book category yaratish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Base book category muvofaqiyatli yaratildi"),
            @ApiResponse(responseCode = "409", description = "Ushbu nomli Base book category mavjud")
    })
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody CreateCategoryRequest request) {
        return baseBookCategoryService.create(request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Base book category tahrirlash")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base book category muvofaqiyatli tahrirlandi"),
            @ApiResponse(responseCode = "404", description = "Base book category topilmadi")
    })
    public ResponseEntity<ResponseMessage> update(@PathVariable Integer id, @Valid @RequestBody UpdateCategoryRequest request) {
        return baseBookCategoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Base book category o'chirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base book category muvofaqiyatli o'chirildi"),
            @ApiResponse(responseCode = "404", description = "Base book category topilmadi"),
            @ApiResponse(responseCode = "400", description = "Base book category o'chirib bo'lmaydi, chunki u hali ham kitoblar bilan bog'liq")
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        return baseBookCategoryService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Base book category ro'yxatini olish")
    public ResponseEntity<ResponseMessage> getBaseBookCategoryList(@RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return baseBookCategoryService.getList(sortDirection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Base book category ID bo'yicha olish")
    @ApiResponse(responseCode = "404", description = "Base book category topilmadi")
    public ResponseEntity<ResponseMessage> getBaseBookCategoryById(@PathVariable Integer id) {
        return baseBookCategoryService.get(id);
    }
}
