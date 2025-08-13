package aifu.project.libraryweb.controller.admin_controller.pdf_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.pdf_book_dto.FileUploadResponseDTO;
import aifu.project.libraryweb.service.pdf_book_service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
@Tag(name = "Admin: File Upload Management", description = "Rasmlar va PDF fayllarni yuklash uchun API'lar")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Rasm faylini yuklash",
            description = "Berilgan rasm faylini serverga yuklaydi va uning URL manzilini qaytaradi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rasm muvaffaqiyatli yuklandi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "400", description = "Yaroqsiz fayl (formati, hajmi yoki nomi noto'g'ri)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "500", description = "Faylni saqlashda serverda xatolik",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)))
    })
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadImage(@RequestPart("file") MultipartFile file) {
        // 'throws' olib tashlandi. Barcha xatoliklar GlobalExceptionHandler orqali ushlanadi.
        String url = fileStorageService.save(file, "image");
        ResponseMessage body = new ResponseMessage(
                true,
                "Image successfully uploaded",
                Map.of("url", url)
        );
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "PDF faylini yuklash",
            description = "Berilgan PDF faylini serverga yuklaydi va uning URL manzilini hamda hajmini (MB) qaytaradi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF muvaffaqiyatli yuklandi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "400", description = "Yaroqsiz fayl (formati, hajmi yoki nomi noto'g'ri)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "500", description = "Faylni saqlashda serverda xatolik",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)))
    })
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadPdf(@RequestPart("file") MultipartFile file) {
        // 'throws' olib tashlandi.
        FileUploadResponseDTO response = fileStorageService.saveWithSize(file, "pdf");
        ResponseMessage body = new ResponseMessage(
                true,
                "PDF successfully uploaded",
                response
        );
        return ResponseEntity.ok(body);
    }
}