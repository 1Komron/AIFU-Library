package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.history_service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    @Operation(summary = "Qaytarilgan kitoblar tarixi ro'yxatini olish")
    @ApiResponse(responseCode = "200", description = "Tarix muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getHistory(@RequestParam(defaultValue = "1") int pageNumber,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return historyService.getAll(pageNumber, pageSize, sortDirection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID bo'yicha tarix ma'lumotini olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarix ma'lumotlari muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "404", description = "Tarix ma'lumoti topilmadi")
    })
    public ResponseEntity<ResponseMessage> getHistoryById(@PathVariable Long id) {
        return historyService.getById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Qidiruv so'rovi bo'yicha tarix ma'lumotlarini olish",
            description = """
                    Parametrlar:
                    - field: Qidiruv maydoni ('userID' 'cardNumber', 'inventoryNumber')
                    - query: Qidiruv so'zi
                    - pageNumber: Sahifa raqami (default: 1)
                    - pageSize: Sahifa hajmi (default: 10)
                    - sortDirection: Tartiblash yo'nalishi (default: 'asc') 'asc' yoki 'desc'
                    
                    Eslatma: 'userID' faqat son bolishi kerak, yani student ID bo'lishi kerak.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qidiruv natijalari muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri field yoki so'rov yuborildi")
    })
    public ResponseEntity<ResponseMessage> searchHistory(@RequestParam String field,
                                                         @RequestParam String query,
                                                         @RequestParam(defaultValue = "1") int pageNumber,
                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                         @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return historyService.search(field, query, pageNumber, pageSize, sortDirection);
    }
}
