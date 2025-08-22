package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.service.student_service.ImportErrorReportExcelService;
import aifu.project.libraryweb.service.student_service.StudentExcelImportService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "Talabalarni Import Qilish (Super Admin)")
@RestController
@RequestMapping("/api/super-admin/students/import")
@RequiredArgsConstructor
@Slf4j

public class StudentImportController {

    private final ImportErrorReportExcelService importErrorReportExcelService;
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


    @Operation(summary = "Excel faylni yuklab, talabalarni import qilish")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseMessage> uploadStudents(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("'{}' nomli fayl orqali talabalarni import qilish so'rovi qabul qilindi.", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMessage(false, "Fayl bo'sh bo'lishi mumkin emas.", null));
        }

        ImportStats stats = studentExcelImportService.importStudentsFromExcel(file.getInputStream());

        String message = String.format("%d ta talaba muvaffaqiyatli import qilindi. %d ta yozuvda xatolik aniqlandi.",
                stats.getSuccessCount(),
                stats.getFailedRecords() != null ? stats.getFailedRecords().size() : 0);

        log.info(message);
        return ResponseEntity.ok(new ResponseMessage(true, message, stats));
    }
    @Operation(summary = "Import xatoliklari haqida Excel-hisobot yuklab olish")
    @PostMapping("/report")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadImportErrorReport(@RequestBody List<ImportErrorDTO> failedRecords) {
        log.info("{} ta yozuvdan iborat import xatoliklari hisobotini generatsiya qilish so'rovi.", failedRecords.size());
        if (failedRecords.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] excelFile = importErrorReportExcelService.generateStudentImportErrorReport(failedRecords);
            String fileName = "import_xatoliklari_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // Fayl yuklash uchun standart media type
                    .body(excelFile);

        } catch (IOException e) {
            log.error("Import xatoliklari hisobotini generatsiya qilishda xatolik!", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
