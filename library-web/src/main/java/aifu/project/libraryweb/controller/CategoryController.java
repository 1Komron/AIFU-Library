package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseMessage> create(
            @Valid @RequestBody CreateCategoryDTO dto) {
        CategoryResponseDTO response = categoryService.create(dto);
        ResponseMessage body = new ResponseMessage(
                true,
                "Category muvaffaqiyatli yaratildi",
                        response
        );
        
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCategoryDTO dto) {
        CategoryResponseDTO response = categoryService.update(id, dto);
        ResponseMessage body = new ResponseMessage(
                true,
                "Category muvaffaqiyatli uzgartirildi",
                response
        );
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        try {
            categoryService.delete(id);
            ResponseMessage response = new ResponseMessage(
                    true,
                    "Category muvaffaqiyatli o'chirildi",
                    null
            );
            return ResponseEntity.ok(response); // yoki HttpStatus.NO_CONTENT ham bo'lishi mumkin
        } catch (IllegalStateException e) {
            ResponseMessage response = new ResponseMessage(
                    false,
                    "Xatolik yuz berdi: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException e) {
            ResponseMessage response = new ResponseMessage(
                    false,
                    "Category topilmadi: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @GetMapping("/{id}/pdf-books")
    public ResponseEntity<ResponseMessage> getBooksByCategoryId(@PathVariable Integer id) {
        try {
            List<PdfBookPreviewDTO> books = categoryService.getBooksByCategoryId(id);
            ResponseMessage response = new ResponseMessage(
                    true,
                    "Ushbu categoryga tegishli kitoblar muvaffaqiyatli olindi",
                    books
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseMessage response = new ResponseMessage(
                    false,
                    "Category topilmadi yoki unga tegishli kitoblar yo'q: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}