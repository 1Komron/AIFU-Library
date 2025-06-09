package aifu.project.libraryweb.controller.BaseBookController;

import aifu.project.commondomain.dto.live_dto.BaseBookDTO;

import aifu.project.libraryweb.service.base_book_Service.BaseBookService;
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
    public ResponseEntity<BaseBookDTO> create(@Valid @RequestBody BaseBookDTO dto) {
        return ResponseEntity.ok(baseBookService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseBookDTO> update(@PathVariable Integer id, @Valid @RequestBody BaseBookDTO dto) {
        return ResponseEntity.ok(baseBookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        baseBookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseBookDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(baseBookService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<BaseBookDTO>> getAll() {
        return ResponseEntity.ok(baseBookService.getAll());
    }
}