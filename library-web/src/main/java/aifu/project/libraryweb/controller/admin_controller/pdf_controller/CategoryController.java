package aifu.project.libraryweb.controller.admin_controller.pdf_controller;

import aifu.project.common_domain.dto.pdf_book_dto.*;
import aifu.project.common_domain.entity.Category;
import aifu.project.common_domain.mapper.CategoryMapper;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @PostMapping
    @Operation( summary = "Yangi PDF category yaratish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli yaratildi"),
            @ApiResponse(responseCode = "400", description = "Yaratishda xatolik"),
            @ApiResponse(responseCode = "409", description = "Category allaqachon mavjud")
    })
    public ResponseEntity<ResponseMessage> create( @RequestBody CreateCategoryDTO dto) {
        return categoryService.create(dto);
    }


    @PutMapping("/{id}")
    @Operation (summary = "PDF category ni yangilash")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = "Muvaffaqiyatli yangilandi"),
            @ApiResponse (responseCode = "404", description = "Category topilmadi"),
            @ApiResponse(responseCode = "409", description = "Nom boshqa category bilan to‘qnashmoqda")
    })
    public ResponseEntity<ResponseMessage> update(@PathVariable Integer id, @RequestBody UpdateCategoryDTO dto) {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "PDF Category o'chirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category muvaffaqiyatli o‘chirildi"),
            @ApiResponse(responseCode = "404", description = "Category topilmadi"),
            @ApiResponse(responseCode = "400", description = "Category o‘chirib bo‘lmaydi (bog‘liq ma’lumotlar mavjud)")
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }


    @GetMapping
    @Operation(summary = "PDF Category ro'yxatini olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Barcha categorylar muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "400", description = "Sort direction noto‘g‘ri formatda"),
            @ApiResponse(responseCode = "500", description = "Server xatosi")
    })
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return categoryService.getAll(sortDirection);
    }


    @GetMapping("/{id}")
    @Operation(summary = "PDF Category ni ID bo'yicha olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "404", description = "ID boyicha category topilmadi")
    })
    public ResponseEntity<ResponseMessage> getOne(@PathVariable Integer id) {
        return categoryService.get(id);

    }

}
