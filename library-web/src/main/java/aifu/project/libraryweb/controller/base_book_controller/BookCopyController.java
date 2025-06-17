package aifu.project.libraryweb.controller.base_book_controller;

import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.dto.live_dto.BookCopyResponseDTO;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/book-copies")
@RequiredArgsConstructor
public class BookCopyController {
    private final BookCopyService bookCopyService;

    @PostMapping
    public ResponseEntity<BookCopyResponseDTO> create(@Valid @RequestBody BookCopyCreateDTO dto) {
        return ResponseEntity.ok(bookCopyService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookCopyResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody BookCopyCreateDTO dto) {
        return ResponseEntity.ok(bookCopyService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookCopyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCopyResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookCopyService.getOne(id));
    }

    @GetMapping("/base-book/{baseBookId}")
    public ResponseEntity<List<BookCopyResponseDTO>> getAllByBaseBook(@PathVariable Integer baseBookId) {
        return ResponseEntity.ok(bookCopyService.getAllByBaseBook(baseBookId));
    }

    @GetMapping
    public ResponseEntity<List<BookCopyResponseDTO>> getAll() {
        return ResponseEntity.ok(bookCopyService.getAll());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(bookCopyService.count());
    }
}