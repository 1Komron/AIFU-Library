package aifu.project.libraryweb.controller;

import aifu.project.libraryweb.dto.BookCopyDTO;
import aifu.project.libraryweb.service.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

    @Autowired
    private BookCopyService bookCopyService;

    @PostMapping
    public BookCopyDTO createBookCopy(@RequestBody BookCopyDTO bookCopyDTO) {
        return bookCopyService.createBookCopy(bookCopyDTO);
    }

    @GetMapping("/{id}")
    public BookCopyDTO getBookCopyById(@PathVariable Integer id) {
        return bookCopyService.getBookCopyById(id);
    }

    @GetMapping
    public List<BookCopyDTO> getAllBookCopies() {
        return bookCopyService.getAllBookCopies();
    }

    @PutMapping("/{id}")
    public BookCopyDTO updateBookCopy(@PathVariable Integer id, @RequestBody BookCopyDTO bookCopyDTO) {
        return bookCopyService.updateBookCopy(id, bookCopyDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBookCopy(@PathVariable Integer id) {
        bookCopyService.deleteBookCopy(id);
    }
}