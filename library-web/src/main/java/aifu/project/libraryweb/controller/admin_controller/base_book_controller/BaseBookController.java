package aifu.project.libraryweb.controller.admin_controller.base_book_controller;

import aifu.project.common_domain.dto.live_dto.BaseBookCreateDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BaseBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/base-books")
@RequiredArgsConstructor
public class BaseBookController {
    private final BaseBookService baseBookService;

    @PostMapping
    @Operation(summary = "Base book yaratish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Base book yaratildi"),
            @ApiResponse(responseCode = "404", description = "Base book category topilmadi")
    })
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody BaseBookCreateDTO dto) {
        return baseBookService.create(dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Base book tahrirlash",
            description = """
                    Tahrilash uchun quyidagi fieldlarni yuborish mumkin:
                    Fieldlar: 'title','author', 'series', 'titleDetails', 'publicationYear',
                    'publisher', 'publicationCity', 'isbn', 'pageCount', 'language', 'udc', 'category'
                    
                    Eslatma: 'category' field faqat son bolishi kerak, ya'ni BaseBookCategory IDsi.
                            publicationYear va pageCount fieldlari ham faqat son bolishi kerak.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base book muvaffaqiyatli tahrirlandi"),
            @ApiResponse(responseCode = "404", description = "Base book topilmadi | Base book category topilmadi"),
            @ApiResponse(responseCode = "400", description = "Mavjud bolmagan fieldni tahrirlanishga urinish")
    })
    public ResponseEntity<ResponseMessage> updateBaseBook(@PathVariable Integer id,
                                                          @RequestBody Map<String, Object> updates) {
        return baseBookService.update(id, updates);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Base book o'chirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base book muvaffaqiyatli o'chirildi"),
            @ApiResponse(responseCode = "404", description = "Base book topilmadi"),
            @ApiResponse(responseCode = "400", description = "Ushbu base bookda book copy mavjud, o'chirish mumkin emas")
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        return baseBookService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Base book ma'lumotlarini ID orqagli olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base book ma'lumotlari muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "404", description = "Base book topilmadi")
    })
    public ResponseEntity<ResponseMessage> getById(@PathVariable Integer id) {
        return baseBookService.get(id);
    }

    @GetMapping
    @Operation(summary = "Base book ro'yaxtini olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base book ro'yxati muvaffaqiyatli olindi")
    })
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        return baseBookService.getAll(pageNumber, pageSize);
    }

    @GetMapping("/search")
    @Operation(summary = "Base book qidirish",
            description = """
                    Qidirish query va field orqali amalga oshiriladi.
                    Field: 'id', 'category', 'title', 'author', 'isbn', 'udc', 'series'.
                    Sort direction: 'asc' yoki 'desc'.
                    
                    Eslatma: 'category' va 'id' fieldlari uchun query sifatida ID raqamini kiritish kerak !
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base book qidirish muvaffaqiyatli amalga oshirildi"),
            @ApiResponse(responseCode = "400", description = "Notogri query yoki field kiritildi")
    })
    public ResponseEntity<ResponseMessage> search(@RequestParam String query,
                                                  @RequestParam String field,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(defaultValue = "asc") String sortDirection) {
        return baseBookService.search(query, field, pageNumber, pageSize, sortDirection);
    }
}