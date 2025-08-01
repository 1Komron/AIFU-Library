package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.common_domain.dto.PasswordResetConfirmRequest;
import aifu.project.common_domain.dto.PasswordResetRequest;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.libraryweb.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/password-reset")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/initiate")
    @Operation(summary = "Parolni tiklash uchun tasdiqlash kodi yuborish",
            description = "Berilgan emailga tasdiqlash kodi yuboriladi.")
    public ResponseEntity<ResponseMessage> initiateReset(@Valid @RequestBody PasswordResetRequest request) {
        log.info("Parolni tiklash so‘rovi email uchun: {}", request.getEmail());
        passwordResetService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(
                new ResponseMessage(true, "Parolni tiklash uchun tasdiqlash kodi yuborildi.", null)
        );
    }


    @PostMapping("/confirm")
    @Operation(summary = "Parolni tiklashni tasdiqlash",
            description = "Email, tasdiqlash kodi va yangi parol yordamida parolni yangilaydi.")
    public ResponseEntity<ResponseMessage> confirmReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        log.info("Parolni tasdiqlash so‘rovi email uchun: {}", request.getEmail());
        passwordResetService.confirmPasswordReset(request);
        return ResponseEntity.ok(
                new ResponseMessage(true, "Parolingiz muvaffaqiyatli tiklandi." +
                        " Endi tizimga yangi parol bilan kirishingiz mumkin.", null)
        );
    }
}
