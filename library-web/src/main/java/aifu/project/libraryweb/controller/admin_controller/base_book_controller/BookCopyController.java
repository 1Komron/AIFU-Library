package aifu.project.libraryweb.controller.admin_controller.base_book_controller;

import aifu.project.common_domain.dto.live_dto.BookCopyCreateDTO;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/book-copies")
@RequiredArgsConstructor
public class BookCopyController {
    private final BookCopyService bookCopyService;

    @PostMapping
    @Operation(summary = "Book copy yaratish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book copy yaratildi"),
            @ApiResponse(responseCode = "404", description = "Base book topilmadi")
    })
    public ResponseEntity<ResponseMessage> create(@Valid @RequestBody BookCopyCreateDTO dto) {
        return bookCopyService.create(dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Book copy tahrirlash",
            description = """
                    Tahrirlash uchun quyidagi fieldlarni yuborish mumkin:
                    Fieldlar: 'inventoryNumber', 'shelfLocation', 'notes', 'book'
                    Eslatma: 'book' field faqat son bolishi kerak, ya'ni BaseBook IDsi.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book copy muvaffaqiyatli tahrirlandi"),
            @ApiResponse(responseCode = "404", description = "Book copy | Base Book topilmadi"),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri field | qiymat")
    })
    public ResponseEntity<ResponseMessage> update(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        return bookCopyService.update(id, updates);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Book copy o'chirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book copy muvaffaqiyatli o'chirildi"),
            @ApiResponse(responseCode = "400", description = "Book copy o'chirishda xatolik. Book copy studentda"),
            @ApiResponse(responseCode = "404", description = "Book copy topilmadi")
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        return bookCopyService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Book copy ID bo'yicha olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book copy muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "404", description = "Book copy topilmadi")
    })
    public ResponseEntity<ResponseMessage> getById(@PathVariable Integer id) {
        return bookCopyService.get(id);
    }

    @GetMapping
    @Operation(summary = "Barcha book copylarni olish",
            description = """
                    Barcha book copylarni olish uchun.
                    Parametrlar:
                    - pageNumber: Sahifa raqami (default: 1)
                    - pageSize: Sahifa hajmi (default: 10)
                    - sortDirection: Tartiblash yo'nalishi (default: asc) 'asc' yoki 'desc'
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Barcha book copylar muvaffaqiyatli olindi")
    })
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return bookCopyService.getAll(pageNumber, pageSize, sortDirection);
    }

    @GetMapping("/search")
    @Operation(summary = "Base book bo'yicha book copylarni qidirish",
            description = """
                    Book copylarni qidirish.
                    Parametrlar:
                    - query: Qidiruv so'zi
                    - field: Qidiriladigan field
                    - pageNumber: Sahifa raqami (default: 1)
                    - pageSize: Sahifa hajmi (default: 10)
                    - sortDirection: Tartiblash yo'nalishi (default: asc) 'asc' yoki 'desc'
                    
                    Fieldlar: 'book', 'inventoryNumber', 'epc'
                    Eslatma: 'book' field faqat son bo'lishi kerak, ya'ni BaseBook IDsi.
                    """)

    public ResponseEntity<ResponseMessage> search(@RequestParam String query,
                                                  @RequestParam String field,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return bookCopyService.search(query, field, pageNumber, pageSize, sortDirection);
    }

}