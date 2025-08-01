package aifu.project.libraryweb.controller.clien_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseMessage> getCategories() {
        return categoryService.getAll("asc");
    }
}
