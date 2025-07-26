package aifu.project.common_domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequest {

    @NotBlank(message = "Old password cannot be empty.")
    private String oldPassword;

    @Size(min = 8, message = "New password must be at least 8 characters long.")
    @NotBlank(message = "New password cannot be empty.")
    private String newPassword;

}

