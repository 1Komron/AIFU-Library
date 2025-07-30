package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.PdfBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pdfbooks")
@RequiredArgsConstructor
public class PdfBookController {

    private final PdfBookService pdfBookService;

    @GetMapping("/list")
    public ResponseEntity<ResponseMessage> getList(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "12") int pageSize) {

        Map<String, Object> books = pdfBookService.getList(pageNumber, pageSize);
        ResponseMessage body = new ResponseMessage(
                true,
                "PDF book list retrieved successfully",
                books
        );
        return ResponseEntity.ok(body);
    }



    @PostMapping("/category/{categoryId}")
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
        try {
            PdfBookResponseDTO book = pdfBookService.getOne(id);
            byte[] pdfData = pdfBookService.downloadPdf(id);

            if (pdfData == null || pdfData.length == 0) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            // Fayl nomini tozalash
            String author = book.getAuthor() != null ? book.getAuthor() : "Noma_lumMuallif";
            String title = book.getTitle() != null ? book.getTitle() : "Noma_lumSarlavha";

            String filename = (author + "_" + title)
                    .replaceAll("[^a-zA-Z0-9.-]", "_")
                    .replaceAll("_+", "_")
                    + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename(filename)
                    .build());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfData);

        } catch (Exception ex) {
            // Xatolikni logga yozish (shartli)
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }




    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseMessage> getBooksByCategory(@PathVariable Integer categoryId) {
        try {
            List<PdfBookPreviewDTO> books = pdfBookService.getBooksByCategoryId(categoryId);
            ResponseMessage response = new ResponseMessage(
                    true,
                    "Kitoblar muvaffaqiyatli olindi",
                    books
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(
                    new ResponseMessage(false, "Category topilmadi: " + e.getMessage(), null)
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseMessage> search(@RequestParam(required = false) String value,
                                                  @RequestParam(required = false, defaultValue = "title") String field,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "id") String sortBy,
                                                  @RequestParam(defaultValue = "asc") String sortDir) {

        PdfBookSearchCriteriaDTO criteria = PdfBookSearchCriteriaDTO.builder()
                .value(value)
                .field(field)
                .pageNumber(pageNumber)
                .size(size)
                .sortBy(sortBy)
                .sortDr(sortDir)
                .build();

        Page<PdfBookResponseDTO> result = pdfBookService.search(criteria);
        return ResponseEntity.ok(
                new ResponseMessage(true, "Search completed successfully", result)
        );
    }
}