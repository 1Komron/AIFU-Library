package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.libraryweb.service.student_service.StudentDeactivationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Tag(name = "Talabalarni Deaktivatsiya Qilish (Super Admin)")
@RestController
@RequestMapping("/api/super-admin/students/lifecycle")
@RequiredArgsConstructor
@Slf4j
public class StudentDeletedController {

    private final StudentDeactivationService deactivationService;

    @Operation(summary = "Excel fayl orqali talabalarni ommaviy deaktivatsiya qilish")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deaktivatsiya jarayoni yakunlandi. Natijalar javobning 'data' qismida.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Yaroqsiz so'rov. Sabablari: fayl yuborilmagan yoki fayl formati xato.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "401", description = "Ruxsat yo'q (Autentifikatsiya qilinmagan)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Taqiqlangan ('SUPER_ADMIN' roli yo'q)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Kutilmagan ichki server xatoligi", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class)) })
    })
    @PostMapping(value = "/deactivate-graduates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseMessage> deactivateGraduates(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("'{}' nomli fayl orqali talabalarni deaktivatsiya qilish so'rovi qabul qilindi.", file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            log.warn("Deaktivatsiya muvaffaqiyatsiz: Fayl bo'sh.");
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage(false, "Fayl yuborilishi shart.", null));
        }
        DeactivationStats stats = deactivationService.deactivateStudents(file.getInputStream());
        ResponseMessage response = new ResponseMessage(true, stats.generateResponseMessage(), stats);
        log.info("Deaktivatsiya jarayoni yakunlandi. Muvaffaqiyatsli: {}, Qarzdorlar: {}, Topilmaganlar: {}",
                stats.getSuccessCount(),
                stats.getDebtors() != null ? stats.getDebtors().size() : 0,
                stats.getNotFoundRecords() != null ? stats.getNotFoundRecords().size() : 0);
        return ResponseEntity.ok(response);
    }
}