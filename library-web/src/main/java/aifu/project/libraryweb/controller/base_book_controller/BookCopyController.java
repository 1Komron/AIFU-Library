package aifu.project.libraryweb.controller.base_book_controller;

import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/book-copies")
@RequiredArgsConstructor
public class BookCopyController {
    private final BookCopyService bookCopyService;

    @PostMapping
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody BookCopyCreateDTO dto) {
        return bookCopyService.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessage> update(@PathVariable Integer id, @RequestBody Map<String,Object> updates) {
        return bookCopyService.update(id, updates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        return bookCopyService.delete(id);
    }

    @DeleteMapping
    public ResponseEntity<ResponseMessage> deleteByBaseBook(@RequestParam Integer baseBookId) {
        return bookCopyService.deleteByBaseBook(baseBookId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getById(@PathVariable Integer id) {
        return bookCopyService.getOne(id);
    }

    @GetMapping("/base-book/{id}")
    public ResponseEntity<ResponseMessage> getAllByBaseBook(@PathVariable Integer id,
                                                            @RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        return bookCopyService.getAllByBaseBook(id, pageNumber, pageSize);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        return bookCopyService.getAll(pageNumber, pageSize);
    }
}