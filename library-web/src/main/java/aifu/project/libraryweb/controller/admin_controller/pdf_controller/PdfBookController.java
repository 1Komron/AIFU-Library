package aifu.project.libraryweb.controller.admin_controller.pdf_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookCreateDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookPreviewDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookResponseDTO;
import aifu.project.common_domain.dto.pdf_book_dto.PdfBookSearchCriteriaDTO;
import aifu.project.libraryweb.service.pdf_book_service.PdfBookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/pdf-books")
@RequiredArgsConstructor
@Tag(name = "Admin: PDF Book Management", description = "PDF kitoblarni boshqarish uchun API'lar")
public class PdfBookController {

    private final PdfBookService pdfBookService;

    @Operation(summary = "Yangi PDF kitob yaratish",
            description = "Berilgan kategoriya ID'si va kitob ma'lumotlari asosida yangi PDF kitob yaratadi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitob muvaffaqiyatli yaratildi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "400", description = "Yaroqsiz so'rov (masalan, validatsiya xatosi)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "Kategoriya topilmadi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody PdfBookCreateDTO dto) {
        PdfBookResponseDTO response = pdfBookService.create(dto);
        ResponseMessage body = new ResponseMessage(
                true,
                "PDF book successfully created",
                response
        );
        return ResponseEntity.ok(body);
    }


    @Operation(summary = "Mavjud PDF kitobni yangilash",
            description = "Berilgan ID bo'yicha PDF kitob ma'lumotlarini qisman yangilaydi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitob muvaffaqiyatli yangilandi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "400", description = "Yaroqsiz so'rov (validatsiya xatosi)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "Yangilanadigan kitob yoki uning yangi kategoriyasi topilmadi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessage> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {

        PdfBookResponseDTO updatedBook = pdfBookService.update(id, updates);

        ResponseMessage body = new ResponseMessage(
                true, "Pdf book successfully updated", updatedBook);
        return ResponseEntity.ok(body);
    }


    @Operation(summary = "ID bo'yicha bitta PDF kitobni olish",
            description = "Berilgan ID'ga mos keluvchi PDF kitob haqida to'liq ma'lumot qaytaradi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitob ma'lumotlari muvaffaqiyatli olindi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "Berilgan ID bo'yicha kitob topilmadi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)))
    })


    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getOne(@PathVariable Integer id) {
        PdfBookResponseDTO book = pdfBookService.getOne(id);
        ResponseMessage body = new ResponseMessage(
                true, "PDF book successfully retrieved", book);
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "ID bo'yicha PDF kitobni o'chirish",
            description = "Berilgan ID'ga ega PDF kitobni tizimdan o'chiradi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitob muvaffaqiyatli o'chirildi"),
            @ApiResponse(responseCode = "404", description = "O'chiriladigan kitob topilmadi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)))
    })


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        pdfBookService.delete(id);
        ResponseMessage body = new ResponseMessage(
                true, "PDF book successfully deleted", null);
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "PDF kitob faylini yuklab olish",
            description = "ID bo'yicha PDF kitobning faylini (byte array) yuklab olish uchun xizmat qiladi. " +
                    "Javob JSON emas, balki to'g'ridan-to'g'ri fayl bo'ladi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fayl muvaffaqiyatli yuklab olindi",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)),
            @ApiResponse(responseCode = "404", description = "Kitob yoki unga biriktirilgan fayl URL'i topilmadi"),
            @ApiResponse(responseCode = "500", description = "Faylni yuklab olishda serverda xatolik yuz berdi")
    })


    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Integer id) {
        PdfBookResponseDTO book = pdfBookService.getOne(id);
        byte[] pdfData = pdfBookService.downloadPdf(id);

        String author = book.getAuthor() != null ? book.getAuthor() : "UnknownAuthor";
        String title = book.getTitle() != null ? book.getTitle() : "UnknownTitle";
        String filename = (author + "_" + title)
                .replaceAll("[^a-zA-Z0-9.-]", "_")
                .replaceAll("_+", "_")
                + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
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

        Map<String, Object> result = pdfBookService.getAll(criteria);
        return ResponseEntity.ok(
                new ResponseMessage(true, "Search completed successfully", result)
        );
    }

}