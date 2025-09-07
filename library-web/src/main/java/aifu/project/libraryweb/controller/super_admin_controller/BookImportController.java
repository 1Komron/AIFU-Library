package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.base_book_service.BaseBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/super-admin/book/import")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class BookImportController {
    private final BaseBookService baseBookService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Excel orqali base booklarni yuklash")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel muvaffaqiyatli yuklandi va kitoblar qo'shildi"),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri formatdagi fayl"),
            @ApiResponse(responseCode = "500", description = "Serverdagi xatolik")
    })
    public ResponseEntity<ResponseMessage> uploadExcel(@RequestParam("file") MultipartFile file) {
        return baseBookService.importFromExcel(file);
    }

    @GetMapping(path = "/template", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Excel shablonini yuklab olish")
    @ApiResponse(responseCode = "200", description = "Excel muvaffaqiyatli yuklandi va kitoblar qo'shildi")
    @ApiResponse(responseCode = "204", description = "Shablon majud emas")

    public ResponseEntity<byte[]> templateExcel() {
        return baseBookService.templateFromExcel();
    }
}
