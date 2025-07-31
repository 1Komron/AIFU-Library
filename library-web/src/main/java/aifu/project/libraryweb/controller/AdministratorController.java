/*package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.AdminCreateRequest;
import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.dto.PasswordChangeRequest;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.AdminManagementService;
import aifu.project.libraryweb.service.PasswordManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Bu yagona Controller tizimdagi Administratorlar (Librarian va Admin) bilan bog'liq
 * barcha API operatsiyalarini o'zida jamlaydi: yangi admin yaratish va parolni o'zgartirish.
 *//*

@RestController
@RequestMapping("/api/admins") // Barcha operatsiyalar uchun umumiy manzil
@RequiredArgsConstructor
public class AdministratorController {

    // Ikkala kerakli servisni ham inject qilib olamiz.
    private final AdminManagementService adminManagementService;
    private final PasswordManagementService passwordManagementService;

    */
/**
     * Yangi Admin yaratish uchun endpoint.
     * Bu operatsiyani faqat SUPER_ADMIN rolidagi foydalanuvchi bajara oladi.
     *//*

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ResponseMessage> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        // Asosiy ishni AdminManagementService'ga topshiramiz.
        AdminResponse responseDto = adminManagementService.createAdmin(request);
        ResponseMessage response = new ResponseMessage(true, "Admin muvaffaqiyatli yaratildi", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    */
/**
     * Tizimga kirgan administrator (Librarian yoki Admin) o'z parolini o'zgartirish jarayonini boshlashi uchun endpoint.
     *//*

    @PostMapping("/account/password/change-request")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ResponseMessage> initiatePasswordChange(
            @Valid @RequestBody PasswordChangeRequest request,
            Principal principal) {

        // Asosiy ishni PasswordManagementService'ga topshiramiz.
        passwordManagementService.initiatePasswordChange(principal.getName(), request);
        return ResponseEntity.ok(
                new ResponseMessage(true, "Tasdiqlash kodi " + principal.getName() + " manziliga yuborildi.", null)
        );
    }

    */
/**
     * Parolni o'zgartirishni tasdiqlash uchun endpoint.
     *//*

    */
/*@PostMapping("/account/password/confirm-change")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ResponseMessage> confirmPasswordChange(
            @RequestParam("code") String code,
            Principal principal) {

        passwordManagementService.confirmPasswordChange(principal.getName(), code);
        return ResponseEntity.ok(
                new ResponseMessage(true, "Parolingiz muvaffaqiyatli o'zgartirildi.", null)
        );
    }
}*/
