package aifu.project.libraryweb.controller;

import aifu.project.commondomain.dto.PdfBookDTO;
import aifu.project.libraryweb.service.PdfBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.List;

@RestController
    @RequestMapping("/api/pdfbooks")
@RequiredArgsConstructor
public class PdfBookController {
    private final PdfBookService pdfBookService;
    private final StandardServletMultipartResolver multipartResolver;

    @PostMapping
    public PdfBookDTO create(@RequestParam Integer categoryId, @RequestBody PdfBookDTO dto) {
        return pdfBookService.create(categoryId, dto);
    }

    @GetMapping("/category/{categoryId}")
    public List<PdfBookDTO> getAllByCategory(@PathVariable Integer categoryId) {
        return pdfBookService.getAllByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public PdfBookDTO getOne(@PathVariable Integer id) {

        return pdfBookService.getOne(id);
    }

    @PutMapping("/{id}")
    public PdfBookDTO update(@PathVariable Integer id, @RequestBody PdfBookDTO dto) {
        return pdfBookService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        pdfBookService.delete(id);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Integer id) {
        byte[] pdfData = pdfBookService.downloadPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=book_" + id + ".pdf")
                .body(pdfData);
    }
}