package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.service.student_service.StudentExcelImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Xavfsizlik uchun import
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Tag(name = "Talabalarni Import Qilish (Super Admin)")
@RestController
@RequestMapping("/api/super-admin/students/import")
@RequiredArgsConstructor
@Slf4j

public class StudentImportController {

    private final StudentExcelImportService studentExcelImportService;

    @Operation(summary = "Import uchun Excel shablonini yuklab olish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shablon muvaffaqiyatli yuklab olindi"),
            @ApiResponse(responseCode = "404", description = "Shablon fayli serverda topilmadi"),
            @ApiResponse(responseCode = "500", description = "Ichki server xatoligi")
    })
    @GetMapping("/template")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Resource> downloadTemplate() {
        log.info("Excel shablonini yuklab olish uchun so'rov keldi.");
        try {
            Resource resource = new ClassPathResource("templates/student_import_template.xlsx");
            if (resource.exists()) {
                String filename = "student_import_template.xlsx";
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(resource);
            } else {
                log.error("Excel shabloni topilmadi: 'resources/templates/student_import_template.xlsx'");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Excel shablonini yuklab berishda kutilmagan xatolik", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "To'ldirilgan Excel faylni yuklab, talabalarni import qilish")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Import jarayoni muvaffaqiyatli yakunlandi. Natijalar javobning 'data' qismida.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Yaroqsiz so'rov. Mumkin bo'lgan sabablar: fayl yuborilmagan, fayl formati noto'g'ri (sarlavha yo'q, kerakli ustun topilmadi).",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ruxsat yo'q (Autentifikatsiya qilinmagan)",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Taqiqlangan (Foydalanuvchida 'SUPER_ADMIN' roli yo'q)",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Kutilmagan ichki server xatoligi",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) })
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseMessage> uploadStudents(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("'{}' nomli fayl orqali talabalarni import qilish so'rovi qabul qilindi.", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("Import muvaffaqiyatsiz yakunlandi: Fayl bo'sh.");
            return ResponseEntity.badRequest().body(new ResponseMessage(false, "Fayl bo'sh bo'lishi mumkin emas.", null));
        }
        ImportStats stats = studentExcelImportService.importStudentsFromExcel(file.getInputStream());
        ResponseMessage response = new ResponseMessage(true, stats.generateResponseMessage(), stats);
        log.info("Import jarayoni yakunlandi. Muvaffaqiyatli: {}, Xatolar: {}", stats.getSuccessCount(), stats.getFailedRecords() != null ? stats.getFailedRecords().size() : 0);
        return ResponseEntity.ok(response);
    }
}
