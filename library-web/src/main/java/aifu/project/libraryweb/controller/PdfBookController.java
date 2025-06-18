package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.pdf_book_dto.PdfBookCreateDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookPreviewDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookResponseDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookUpdateDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.PdfBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdfbooks")
@RequiredArgsConstructor
public class PdfBookController {

    private final PdfBookService pdfBookService;

    @GetMapping("/list")
    public ResponseEntity<ResponseMessage> getList(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<PdfBookPreviewDTO> books = pdfBookService.getList(pageNumber, pageSize);
        ResponseMessage body = new ResponseMessage(
                true,
                "PDF book list retrieved successfully",
                books
        );
        return ResponseEntity.ok(body);
    }



    @PostMapping("/{categoryId}")
    public ResponseEntity<ResponseMessage> create

            (@PathVariable Integer categoryId,
            @Valid @RequestBody PdfBookCreateDTO dto){
        PdfBookResponseDTO response = pdfBookService.create(categoryId, dto);
        ResponseMessage body = new ResponseMessage(
                true,
                "PDF book successfully created",
                response
        );
        return ResponseEntity.ok(body);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessage> update(
            @PathVariable Integer id,
            @Valid @RequestBody PdfBookUpdateDTO dto) {
        PdfBookResponseDTO updatedBook = pdfBookService.update(id, dto);
        ResponseMessage body = new ResponseMessage(
                true,
                "Pdf book successfully updated",
                updatedBook
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getOne(@PathVariable Integer id) {
        PdfBookResponseDTO book = pdfBookService.getOne(id);

        if (book == null) {
            return ResponseEntity
                    .status(404)
                    .body(new ResponseMessage(false, "PDF book not found", null));
        }

        return ResponseEntity.ok(
                new ResponseMessage(true, "PDF book successfully retrieved", book)
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        try {
            pdfBookService.delete(id);
            return ResponseEntity.ok(new ResponseMessage(true, "PDF book successfully deleted", null));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ResponseMessage(false, "PDF book not found", null));
        }
    }



    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Integer id) {
        byte[] pdfData = pdfBookService.downloadPdf(id);
        PdfBookResponseDTO book = pdfBookService.getOne(id);
        if (pdfData == null || pdfData.length == 0) {
            return ResponseEntity
                    .status(404)
                    .header("Content-Type", "application/json")
                    .body(null);
        }
        // Muallif va sarlavhadan fayl nomi yaratish
        String author = book.getAuthor() != null ? book.getAuthor() : "Noma'lumMuallif";
        String title = book.getTitle() != null ? book.getTitle() : "Noma'lumSarlavha";
        // Fayl nomini tozalash (maxsus belgilarni olib tashlash)
        String filename = (author + "_" + title)
                .replaceAll("[^a-zA-Z0-9.-]", "_") // Noto'g'ri belgilarni "_" bilan almashtirish
                .replaceAll("_+", "_") // Bir nechta "_" ni bittaga qisqartirish
                + ".pdf";

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(pdfData);
    }

}