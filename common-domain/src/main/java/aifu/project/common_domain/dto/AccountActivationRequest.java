package aifu.project.common_domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Bu DTO SuperAdmin tomonidan yosh Admin akkauntini faollashtirish
 * uchun yuboriladigan so'rovni ifodalaydi.
 * U faollashtirilishi kerak bo'lgan emailni va tasdiqlash kodini o'z ichiga oladi.
 */
@Data
public class AccountActivationRequest {

    /**
     * Faollashtirilishi kerak bo'lgan foydalanuvchining email manzili.
     * Bo'sh bo'lishi mumkin emas va email formatiga mos kelishi kerak.
     */
    @Email(message = "Email formati noto'g'ri.")
    @NotBlank(message = "Email bo'sh bo'lishi mumkin emas.")
    private String email;

    /**
     * Foydalanuvchining emailiga yuborilgan 6 xonali tasdiqlash kodi.
     * Bo'sh bo'lishi mumkin emas.
     */
    @NotBlank(message = "Tasdiqlash kodi bo'sh bo'lishi mumkin emas.")
    private String code;
}