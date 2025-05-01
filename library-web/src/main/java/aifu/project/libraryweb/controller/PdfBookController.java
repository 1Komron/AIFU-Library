package aifu.project.libraryweb.controller;

import aifu.project.libraryweb.dto.PdfBookDTO;
import aifu.project.libraryweb.service.PdfBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdf-books")
@RequiredArgsConstructor
public class PdfBookController {

    private final PdfBookService pdfBookService;

    // ADMIN: Create metadata (after frontend has uploaded files)
    @PostMapping("/admin/create/{categoryId}")
    public PdfBookDTO create(@PathVariable Integer categoryId,
                             @RequestBody PdfBookDTO dto) {
        return pdfBookService.create(categoryId, dto);
    }

    // USER: View all by category
    @GetMapping("/category/{categoryId}")
    public List<PdfBookDTO> getAllByCategory(@PathVariable Integer categoryId) {
        return pdfBookService.getAllByCategory(categoryId);
    }

    // USER: View single
    @GetMapping("/{id}")
    public PdfBookDTO getOne(@PathVariable Integer id) {
        return pdfBookService.getOne(id);
    }

    // ADMIN: Update metadata
    @PutMapping("/admin/{id}")
    public PdfBookDTO update(@PathVariable Integer id,
                             @RequestBody PdfBookDTO dto) {
        return pdfBookService.update(id, dto);
    }

    // ADMIN: Delete
    @DeleteMapping("/admin/{id}")
    public void delete(@PathVariable Integer id) {
        pdfBookService.delete(id);
    }
}
