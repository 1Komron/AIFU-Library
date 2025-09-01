package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.ImportResultDTO;
import aifu.project.libraryweb.service.student_service.ImportErrorReportExcelService;
import aifu.project.libraryweb.service.student_service.StudentExcelImportService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

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





    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Excel faylni yuklab, talabalarni import qilish")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> importStudents(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("'{}' fayli bilan import jarayoni boshlandi.", file.getOriginalFilename());

        ImportResultDTO result = studentExcelImportService.importStudents(file);

        String message = String.format("%d ta talaba muvaffaqiyatli import qilindi. %d ta yozuvda xatolik aniqlandi.",
                result.getSuccessCount(), result.getErrorCount());

        return ResponseEntity.ok(new ResponseMessage(true, message, result));
    }

    @Operation(summary = "Import xatoliklari haqida Excel-hisobot yuklab olish")
    @GetMapping("/import/report/{jobId}")
    public ResponseEntity<Resource> downloadImportErrorReport(@PathVariable UUID jobId) {
        log.info("{} ID'li import jarayonining xatoliklar hisoboti so'raldi.", jobId);

        Map<String, Object> reportData = studentExcelImportService.getErrorReport(jobId);

        byte[] excelFile = (byte[]) reportData.get("file");
        String fileName = (String) reportData.get("fileName");

        ByteArrayResource resource = new ByteArrayResource(excelFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}


