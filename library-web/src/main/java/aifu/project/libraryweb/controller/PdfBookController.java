package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.pdf_book_dto.PdfBookCreateDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookPreviewDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookResponseDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookUpdateDTO;
import aifu.project.libraryweb.service.pdf_book_service.PdfBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdfbooks")
@RequiredArgsConstructor
public class PdfBookController {

    private final PdfBookService pdfBookService;

    @GetMapping("/list")
    public ResponseEntity<List<PdfBookPreviewDTO>> getList(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<PdfBookPreviewDTO> books = pdfBookService.getList(pageNumber, pageSize);
        return ResponseEntity.ok(books);
    }



    @PostMapping("/{categoryId}")
    public ResponseEntity<PdfBookResponseDTO> create(@PathVariable Integer categoryId,@Valid @RequestBody PdfBookCreateDTO dto) {
        PdfBookResponseDTO response = pdfBookService.create(categoryId,dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PdfBookResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody PdfBookUpdateDTO dto) {
        PdfBookResponseDTO updatedBook = pdfBookService.update(id, dto);
        return ResponseEntity.ok(updatedBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PdfBookResponseDTO> getOne(@PathVariable Integer id) {
        PdfBookResponseDTO book = pdfBookService.getOne(id);
        return ResponseEntity.ok(book);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        pdfBookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Integer id) {
        byte[] pdfData = pdfBookService.downloadPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=book_" + id + ".pdf")
                .body(pdfData);
    }
}