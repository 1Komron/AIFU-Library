package aifu.project.libraryweb.controller.base_book_controller;

import aifu.project.common_domain.dto.CreateCategoryRequest;
import aifu.project.common_domain.dto.UpdateCategoryRequest;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.BaseBookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/base-book/categories")
@RequiredArgsConstructor
public class BaseBookCategoryController {
    private final BaseBookCategoryService baseBookCategoryService;

    @PostMapping
    public ResponseEntity<ResponseMessage> create(@RequestBody CreateCategoryRequest request) {
        return baseBookCategoryService.create(request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessage> update(@PathVariable Integer id, @RequestBody UpdateCategoryRequest request) {
        return baseBookCategoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        return baseBookCategoryService.delete(id);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getBaseBookCategoryList() {
        return baseBookCategoryService.getList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getBaseBookCategoryById(@PathVariable Integer id) {
        return baseBookCategoryService.get(id);
    }
}
