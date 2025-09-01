package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.student_dto.DeactivationResultDTO;
import aifu.project.common_domain.dto.student_dto.FileDownloadDTO;
import aifu.project.libraryweb.service.student_service.StudentDeactivationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@Tag(name = "Talabalarni Deaktivatsiya Qilish (Super Admin)")
@RestController
@RequestMapping("/api/super-admin/students/lifecycle")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class StudentDeletedController {

    private final StudentDeactivationService deactivationService;

    @Operation(
            summary = "Excel fayl orqali talabalarni ommaviy deaktivatsiya qilish jarayonini boshlash",
            description = "Excel faylni qabul qilib, undagi talabalarni bazadan qidiradi. Qarzdor bo'lmagan talabalarni deaktivatsiya (yumshoq o'chirish) qiladi. Jarayon natijasi sifatida statistika, jarayon ID'si va hisobotlarni yuklab olish uchun URL manzillarini qaytaradi."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deaktivatsiya jarayoni muvaffaqiyatli boshlandi. Natijalar javobda.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "400", description = "Yaroqsiz so'rov (fayl yuborilmagan yoki bo'sh).", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autentifikatsiya qilinmagan.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Taqiqlangan (SUPER_ADMIN roli yo'q).", content = @Content),
            @ApiResponse(responseCode = "500", description = "Kutilmagan ichki server xatoligi.", content = @Content)
    })
    @PostMapping(value = "/deactivate-graduates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> startDeactivationProcess(@RequestParam("file") MultipartFile file) throws IOException {
        DeactivationResultDTO result = deactivationService.startDeactivationProcess(file);
        String message = String.format("Deaktivatsiya jarayoni yakunlandi. Muvaffaqiyatli: %d, Qarzdorlar: %d, Topilmaganlar: %d",
                result.getSuccessCount(), result.getDebtorCount(), result.getNotFoundCount());
        return ResponseEntity.ok(new ResponseMessage(true, message, result));
    }

    @Operation(
            summary = "Deaktivatsiya qilinmagan qarzdor talabalar hisobotini yuklab olish",
            description = "Deaktivatsiya jarayonining jobId'si orqali kitob qarzi borligi sababli o'chirilmagan talabalar ro'yxatini Excel fayl ko'rinishida yuklab olish."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel hisoboti muvaffaqiyatli generatsiya qilindi.",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Berilgan jobId bo'yicha hisobot topilmadi.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autentifikatsiya qilinmagan.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Taqiqlangan (SUPER_ADMIN roli yo'q).", content = @Content)
    })
    @GetMapping("/report/debtors/{jobId}")
    public ResponseEntity<Resource> downloadDebtorsReport(@PathVariable UUID jobId) {
        FileDownloadDTO fileDto = deactivationService.getDebtorsReport(jobId);
        return createExcelResponse(fileDto);
    }

    @Operation(
            summary = "Deaktivatsiya jarayonida topilmagan talabalar hisobotini yuklab olish",
            description = "Deaktivatsiya jarayonining jobId'si orqali Excel faylda ko'rsatilgan, lekin ma'lumotlar bazasida topilmagan yoki allaqachon o'chirilgan talabalar ro'yxatini Excel fayl ko'rinishida yuklab olish."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel hisoboti muvaffaqiyatli generatsiya qilindi.",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Berilgan jobId bo'yicha hisobot topilmadi.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autentifikatsiya qilinmagan.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Taqiqlangan (SUPER_ADMIN roli yo'q).", content = @Content)
    })
    @GetMapping("/report/not-found/{jobId}")
    public ResponseEntity<Resource> downloadNotFoundReport(@PathVariable UUID jobId) {
        FileDownloadDTO fileDto = deactivationService.getNotFoundReport(jobId);
        return createExcelResponse(fileDto);
    }

    // Bu yordamchi metodni private qilib qoldiramiz, chunki u faqat shu klass ichida ishlatiladi.
    private ResponseEntity<Resource> createExcelResponse(FileDownloadDTO fileDto) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileDto.getResource().contentLength())
                .body(fileDto.getResource());
    }
}