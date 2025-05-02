package aifu.project.libraryweb.controller;

import aifu.project.libraryweb.dto.PdfBookDTO;
import aifu.project.libraryweb.service.PdfBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdf-books")
@RequiredArgsConstructor
public class PdfBookController {

    private final PdfBookService pdfBookService;

    // Admin uchun kitob yaratish
    @PostMapping("/admin/create/{categoryId}")
    public PdfBookDTO create(@PathVariable Integer categoryId,
                             @RequestBody PdfBookDTO dto) {
        return pdfBookService.create(categoryId, dto);
    }

    // Admin uchun kitobni yangilash
    @PutMapping("/admin/{id}")
    public PdfBookDTO update(@PathVariable Integer id,
                             @RequestBody PdfBookDTO dto) {
        return pdfBookService.update(id, dto);
    }

    // Admin uchun kitobni o'chirish
    @DeleteMapping("/admin/{id}")
    public void delete(@PathVariable Integer id) {
        pdfBookService.delete(id);
    }

    // Barcha kitoblarni ko‘rish (Admin va User uchun)
    @GetMapping("/category/{categoryId}")
    public List<PdfBookDTO> getAllByCategory(@PathVariable Integer categoryId) {
        return pdfBookService.getAllByCategory(categoryId);
    }

    // Foydalanuvchi uchun kitobni ko‘rish
    @GetMapping("/{id}")
    public PdfBookDTO getOne(@PathVariable Integer id) {
        return pdfBookService.getOne(id);
    }

    // Foydalanuvchi uchun kitobni yuklab olish
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Integer id) {
        byte[] pdfContent = pdfBookService.downloadPdf(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"book" + id + ".pdf\"")
                .body(pdfContent);
    }
}
