package aifu.project.common_domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetConfirmRequest {

    @Email(message = "Email format is invalid.")
    @NotBlank(message = "Email cannot be empty.")
    private String email;

    @NotBlank(message = "Tasdiqlash kodi bo'sh bo'lishi mumkin emas.")
    private String code;

    /*@NotBlank(message = "Yangi parol bo'sh bo'lishi mumkin emas.")
    @Size(min = 8, message = "Yangi parol kamida 8 ta belgidan iborat bo'lishi kerak.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Yangi parolda kamida bitta harf va bitta raqam bo'lishi shart.")*/
    private String newPassword;
}
