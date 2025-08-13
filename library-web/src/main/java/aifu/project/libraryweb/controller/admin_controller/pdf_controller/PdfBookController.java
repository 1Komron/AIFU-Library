package aifu.project.libraryweb.controller.admin_controller.pdf_controller;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.PdfBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/pdf-books")
@RequiredArgsConstructor
public class PdfBookController {

    private final PdfBookService pdfBookService;

    @PostMapping
    @Operation(summary = "PDF kitob yaratish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF book muvaffaqiyatli yaratildi"),
            @ApiResponse(responseCode = "400", description = "Yaratish jarayonida xatolik yuz berdi"),
            @ApiResponse(responseCode = "404", description = "Kategoriya topilmadi")
    })
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody PdfBookCreateDTO dto) {
        PdfBookResponseDTO response = pdfBookService.create(dto);
        ResponseMessage body = new ResponseMessage(
                true,
                "PDF book successfully created",
                response
        );
        return ResponseEntity.ok(body);
    }


    @PatchMapping("/{id}")
    @Operation(summary = "PDF kitob ma'lumotlarini yangilash",
            description = "Berilgan ID bo'yicha PDF kitob ma'lumotlarini yangilaydi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF book muvaffaqiyatli yangilandi"),
            @ApiResponse(responseCode = "400", description = "Field noto'g'ri yoki yangilash jarayonida xatolik yuz berdi"),
    })
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
    @Operation(summary = "PDF kitob ma'lumotlarini ID bo'yicha olish",
            description = "Berilgan ID bo'yicha PDF kitob ma'lumotlarini qaytaradi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF book muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "404", description = "PDF book topilmadi")
    })
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
    @Operation(summary = "PDF kitobni o'chirish",
            description = "Berilgan ID bo'yicha PDF kitobni o'chiradi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF book muvaffaqiyatli o'chirildi"),
            @ApiResponse(responseCode = "404", description = "PDF book topilmadi")
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {

        try {
            pdfBookService.delete(id);
            return ResponseEntity.ok(new ResponseMessage(true, "PDF book successfully deleted", null));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ResponseMessage(false, "PDF book not found", null));
        }
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "PDF kitobni yuklab olish",
            description = "Berilgan ID bo'yicha PDF kitobni yuklab olish uchun so'rov yuboradi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF book muvaffaqiyatli yuklab olindi"),
            @ApiResponse(responseCode = "404", description = "PDF book topilmadi")
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
            log.error("Error downloading PDF book with ID {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping
    @Operation(summary = "PDF kitoblarni ro'yxatini olish",
            description = """
                    PDF kitoblarni qidirish uchun so'rov yuboradi.
                    Parametrlar:
                    - field: Qidirish maydoni (masalan, 'fullInfo', 'categoryId')
                    - query: Qidirish so'zi
                    - pageNumber: Sahifa raqami (default: 1)
                    - pageSize: Sahifadagi elementlar soni (default: 10)
                    - sortDirection: Tartiblash yo'nalishi ('asc' yoki 'desc', default: 'asc')
                    Eslatma: categoryId doim son bolishi kerak.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF kitoblar ro'yxati muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "400", description = "Qidirish so'rovi noto'g'ri")
    })
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(required = false) String field,
                                                  @RequestParam(required = false) String query,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(defaultValue = "asc") String sortDirection) {

        PdfBookSearchCriteriaDTO criteria = PdfBookSearchCriteriaDTO.builder()
                .query(query)
                .field(field)
                .pageNumber(pageNumber)
                .size(pageSize)
                .sortDr(sortDirection)
                .build();

        Page<PdfBookResponseDTO> result = pdfBookService.getAll(criteria);
        return ResponseEntity.ok(
                new ResponseMessage(true, "Search completed successfully", result)
        );
    }
}