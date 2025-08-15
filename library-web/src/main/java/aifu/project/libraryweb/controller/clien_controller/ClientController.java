package aifu.project.libraryweb.controller.clien_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookResponseDTO;
import aifu.project.libraryweb.lucene.LuceneSearchService;
import aifu.project.libraryweb.service.pdf_book_service.CategoryService;
import aifu.project.libraryweb.service.pdf_book_service.PdfBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final CategoryService categoryService;
    private final LuceneSearchService searchService;
    private final PdfBookService pdfBookService;

    @GetMapping("/categories")
    @Operation(summary = "Kitob category larini olish")
    @ApiResponse(responseCode = "200", description = "Kategoriyalar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getCategories() {
        return categoryService.getAll("asc");
    }

    @GetMapping("/search")
    @Operation(summary = "Kitoblarni qidirish")
    @ApiResponse(responseCode = "200", description = "Qidirish muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> search(@NotNull @RequestParam String query) throws Exception {
        return searchService.searchBooks(query);
    }

    @GetMapping("/pdf-books")
    @Operation(summary = "PDF kitoblarni olish")
    @ApiResponse(responseCode = "200", description = "PDF kitoblar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getPdfBooks(@RequestParam(defaultValue = "1") int pageNumber,
                                                       @RequestParam(defaultValue = "12") int pageSize,
                                                       @RequestParam(required = false) Integer category) {
        Map<String, Object> books = pdfBookService.getList(pageNumber, pageSize, category);
        ResponseMessage body = new ResponseMessage(
                true,
                "PDF book list retrieved successfully",
                books
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/pdf-book/{id}")
    @Operation(summary = "PDF kitobni ID orqali olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF kitob muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "404", description = "PDF kitob topilmadi")
    })
    public ResponseEntity<ResponseMessage> getPdfBookById(@PathVariable Integer id) {
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


    @GetMapping("/download/{id}")
    @Operation(
            summary = "PDF kitobni yuklab olish",
            description = "Berilgan ID orqali PDF formatdagi kitobni fayl sifatida yuklab olish imkonini beradi"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF kitob muvaffaqiyatli yuklab olindi"),
            @ApiResponse(responseCode = "404", description = "PDF kitob topilmadi yoki fayl mavjud emas"),
            @ApiResponse(responseCode = "500", description = "Serverda kutilmagan xatolik yuz berdi")
    })
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

}
