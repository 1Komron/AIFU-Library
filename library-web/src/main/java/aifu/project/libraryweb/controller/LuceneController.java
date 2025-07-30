package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.lucene.LuceneSearchService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class LuceneController {

    private final LuceneSearchService searchService;

    @GetMapping
    public ResponseEntity<ResponseMessage> search(@NotNull @RequestParam String query,
                                                  @NotNull @RequestParam String type) throws Exception {
        return searchService.searchBooks(query, type);
    }
}
