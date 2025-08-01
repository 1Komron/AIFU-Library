package aifu.project.libraryweb.controller.clien_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.lucene.LuceneSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/client/search")
@RequiredArgsConstructor
public class LuceneController {

    private final LuceneSearchService searchService;

    @GetMapping
    @Operation(summary = "Kitoblarni qidirish")
    @ApiResponse(responseCode = "200", description = "Qidirish muvaffaqiyatli bajarildi")
    public ResponseEntity<ResponseMessage> search(@NotNull @RequestParam String query) throws Exception {
        return searchService.searchBooks(query);
    }
}
