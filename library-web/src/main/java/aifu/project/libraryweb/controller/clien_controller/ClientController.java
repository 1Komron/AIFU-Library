package aifu.project.libraryweb.controller.clien_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.lucene.LuceneSearchService;
import aifu.project.libraryweb.service.pdf_book_service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final CategoryService categoryService;
    private final LuceneSearchService searchService;

    @GetMapping("/categories")
    @Operation(summary = "Kitob category larini olish")
    @ApiResponse(responseCode = "200", description = "Kategoriyalar muvaffaqiyatli qaytarildi")
    public ResponseEntity<ResponseMessage> getCategories() {
        return categoryService.getAll("asc");
    }

    @GetMapping
    @Operation(summary = "Kitoblarni qidirish")
    @ApiResponse(responseCode = "200", description = "Qidirish muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> search(@NotNull @RequestParam String query) throws Exception {
        return searchService.searchBooks(query);
    }
}
