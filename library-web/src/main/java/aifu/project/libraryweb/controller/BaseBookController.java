package aifu.project.libraryweb.controller;

import aifu.project.libraryweb.dto.BaseBookDTO;
import aifu.project.libraryweb.service.BaseBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base-books")
public class BaseBookController {

    @Autowired
    private BaseBookService baseBookService;

    @PostMapping
    public BaseBookDTO createBaseBook(@RequestBody BaseBookDTO baseBookDTO) {
        return baseBookService.createBaseBook(baseBookDTO);
    }

    @GetMapping("/{id}")
    public BaseBookDTO getBaseBookById(@PathVariable Integer id) {
        return baseBookService.getBaseBookById(id);
    }

    @GetMapping
    public List<BaseBookDTO> getAllBaseBooks() {
        return baseBookService.getAllBaseBooks();
    }

    @PutMapping("/{id}")
    public BaseBookDTO updateBaseBook(@PathVariable Integer id, @RequestBody BaseBookDTO baseBookDTO) {
        return baseBookService.updateBaseBook(id, baseBookDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBaseBook(@PathVariable Integer id) {
        baseBookService.deleteBaseBook(id);
    }
}