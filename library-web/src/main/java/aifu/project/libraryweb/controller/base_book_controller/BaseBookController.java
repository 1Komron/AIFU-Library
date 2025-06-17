package aifu.project.libraryweb.controller.base_book_controller;

import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookResponseDTO;
import aifu.project.common_domain.dto.live_dto.BaseBookUpdateDTO;
import aifu.project.libraryweb.service.base_book_service.BaseBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/base-books")
@RequiredArgsConstructor
public class BaseBookController {
    private final BaseBookService baseBookService;

    @PostMapping
    public ResponseEntity<BaseBookResponseDTO> create(@Valid @RequestBody BaseBookCreateDTO dto) {
        return ResponseEntity.ok(baseBookService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseBookResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody BaseBookUpdateDTO dto) {
        return ResponseEntity.ok(baseBookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        baseBookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseBookResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(baseBookService.getOne(id));
    }

    @GetMapping
    public ResponseEntity<List<BaseBookResponseDTO>> getAll() {
        return ResponseEntity.ok(baseBookService.getAll());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countBooks() {
        return ResponseEntity.ok(baseBookService.countBooks());
    }
}