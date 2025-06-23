package aifu.project.libraryweb.controller.base_book_controller;

import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BaseBookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/base-books")
@RequiredArgsConstructor
public class BaseBookController {
    private final BaseBookService baseBookService;

    @PostMapping
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody BaseBookCreateDTO dto) {
        return baseBookService.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateBaseBook(@PathVariable Integer id,
                                                          @RequestBody Map<String, Object> updates) {
        return baseBookService.update(id, updates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        return baseBookService.delete(id);
    }

    @DeleteMapping
    public ResponseEntity<ResponseMessage> deleteByCategory(@NotNull @RequestParam Integer categoryId) {
        return baseBookService.deleteByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getById(@PathVariable Integer id) {
        return baseBookService.getOne(id);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        return baseBookService.getAll(pageNumber, pageSize);
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseMessage> getByCategory(@NotNull @RequestParam Integer categoryId,
                                                         @RequestParam(defaultValue = "1") int pageNumber,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        return baseBookService.getByCategory(categoryId,pageNumber, pageSize);
    }
}