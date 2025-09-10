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

    @GetMapping("/{id}")
    @Operation(summary = "ID bo'yicha tarix ma'lumotini olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarix ma'lumotlari muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "404", description = "Tarix ma'lumoti topilmadi")
    })
    public ResponseEntity<ResponseMessage> getHistoryById(@PathVariable Long id) {
        return historyService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Tarix ma'lumotlarini olish",
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
            @ApiResponse(responseCode = "200", description = "Ro'yxat muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri field yoki so'rov yuborildi")
    })
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(required = false) String field,
                                                  @RequestParam(required = false) String query,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(required = false, defaultValue = "desc") String sortDirection) {
        return historyService.getAll(field, query, pageNumber, pageSize, sortDirection);
    }
}
