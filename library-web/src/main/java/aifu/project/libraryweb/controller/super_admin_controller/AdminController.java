package aifu.project.libraryweb.controller.super_admin_controller;

import aifu.project.common_domain.dto.AccountActivationRequest;
import aifu.project.common_domain.dto.AdminCreateRequest;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.AdminManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/super-admin/admins")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {
    private final AdminManagementService adminManagementService;

    @PostMapping
    @Operation(summary = "Admin yaratish")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin muvaffaqiyatli yaratildi"),
            @ApiResponse(responseCode = "409", description = "Bu email bilan allaqachon royxatdan otilgan"),
    })
    public ResponseEntity<ResponseMessage> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return adminManagementService.createAdmin(request);
    }

    @GetMapping
    @Operation(summary = "Barcha adminlarni olish", description = "Sahifalangan va sort qilingan barcha admin foydalanuvchilar ro'yxatini qaytaradi")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "400", description = "Notog'ri so'rov parametrlari (pageNumber, pageSize)"),
            @ApiResponse(responseCode = "401", description = "Autentifikatsiya talab qilinadi"),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q (faqat SUPER_ADMIN kirishi mumkin)"),
            @ApiResponse(responseCode = "500", description = "Serverdagi ichki xatolik")
    })
    public ResponseEntity<ResponseMessage> getAll(@RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                                  @RequestParam(defaultValue = "1") Integer pageNumber,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return adminManagementService.getAll(pageNumber, pageSize, sortDirection);

    }

    @PostMapping("/activate")
    @Operation(
            summary = "Admin akkauntini faollashtirish",
            description = "Yangi yaratilgan va nofaol (`isActive=false`) bo'lgan Admin akkauntini," +
                    " uning emailiga yuborilgan tasdiqlash kodi orqali faollashtiradi. Bu amalni faqat SUPER_ADMIN bajara oladi."
             )

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Akkaunt muvaffaqiyatli faollashtirildi",
                    content =@Content (mediaType = "application/json", schema = @Schema (implementation = ResponseMessage.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Noto'g'ri so'rov. Mumkin bo'lgan sabablar: Tasdiqlash kodi noto'g'ri, kodning muddati o'tgan, yoki akkaunt allaqachon faollashtirilgan.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Autentifikatsiya talab qilinadi (tizimga kirmagan)."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Ruxsat yo'q (bu amalni bajarish uchun SUPER_ADMIN roli talab qilinadi)."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Serverdagi ichki xatolik."
            )
    })
    public ResponseEntity<ResponseMessage> activateAccount(@Valid @RequestBody AccountActivationRequest request) {
        log.info("HTTP Request: POST /api/admins/activate, email: {}", request.getEmail());
        adminManagementService.activateAccount(request);
        return ResponseEntity.ok(new ResponseMessage(true, "Akkaunt muvaffaqiyatli faollashtirildi!", null));
    }






    @DeleteMapping("/{id}")
    @Operation(
            summary = "Adminni o'chirish (yumshoq)",
            description = "Berilgan ID bo'yicha Admin foydalanuvchisini nofaol holatga o'tkazadi (`isDeleted=true`). " +
                    "Yozuv ma'lumotlar bazasidan jismonan o'chirilmaydi. Bu amalni faqat SUPER_ADMIN bajara oladi."
    )
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Admin muvaffaqiyatli o'chirildi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Noto'g'ri so'rov. Mumkin bo'lgan sabablar: Berilgan ID bilan Admin topilmadi yoki u SUPER_ADMIN.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Autentifikatsiya talab qilinadi (tizimga kirmagan)."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Ruxsat yo'q (bu amalni bajarish uchun SUPER_ADMIN roli talab qilinadi)."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Serverdagi ichki xatolik."
            )
    })
    public ResponseEntity<ResponseMessage> deleteAdmin(@PathVariable Long id) {
        log.info("Http Request: DELETE /api/super-admin/admins/{}",id);
        return adminManagementService.deleteAdmin(id);
    }
}