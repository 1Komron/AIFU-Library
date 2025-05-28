package aifu.project.libraryweb.controller.BaseBookController;

import aifu.project.commondomain.dto.BookCopyDTO;
import aifu.project.libraryweb.service.base_book.BookCopyService;
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
    public ResponseEntity<BookCopyDTO> create(@Valid @RequestBody BookCopyDTO dto) {
        return ResponseEntity.ok(bookCopyService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookCopyDTO> update(@PathVariable Integer id, @Valid @RequestBody BookCopyDTO dto) {
        return ResponseEntity.ok(bookCopyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookCopyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCopyDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookCopyService.getById(id));
    }

    @GetMapping("/base-book/{baseBookId}")
    public ResponseEntity<List<BookCopyDTO>> getAllByBaseBook(@PathVariable Integer baseBookId) {
        return ResponseEntity.ok(bookCopyService.getAllByBaseBook(baseBookId));
    }
}