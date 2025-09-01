package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.FileDownloadDTO;
import aifu.project.common_domain.dto.student_dto.ImportResultDTO;
import aifu.project.libraryweb.service.student_service.StudentExcelImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Talabalarni Import Qilish (Super Admin)")
@RestController
@RequestMapping("/api/super-admin/students/import")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class StudentImportController {

    private final StudentExcelImportService studentExcelImportService;

    @Operation(summary = "Import uchun Excel shablonini yuklab olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shablon muvaffaqiyatli yuklab olindi", content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
            @ApiResponse(responseCode = "404", description = "Shablon fayli serverda topilmadi", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ichki server xatoligi", content = @Content)
    })
    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() {
        FileDownloadDTO fileDto = studentExcelImportService.getTemplateFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getFileName() + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(fileDto.getResource().contentLength())
                .body(fileDto.getResource());
    }

    @Operation(summary = "Excel faylni yuklab, talabalarni import qilish")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> importStudents(@RequestParam("file") MultipartFile file) throws IOException {
        ImportResultDTO result = studentExcelImportService.importStudents(file);
        String message = String.format("%d ta talaba muvaffaqiyatli import qilindi. %d ta yozuvda xatolik aniqlandi.",
                result.getSuccessCount(), result.getErrorCount());
        return ResponseEntity.ok(new ResponseMessage(true, message, result));
    }

    @Operation(summary = "Import xatoliklari haqida Excel-hisobot yuklab olish")
    @GetMapping("/report/{jobId}")
    public ResponseEntity<Resource> downloadImportErrorReport(@PathVariable UUID jobId) {
        FileDownloadDTO fileDto = studentExcelImportService.getErrorReport(jobId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileDto.getResource().contentLength())
                .body(fileDto.getResource());
    }
}