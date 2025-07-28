package aifu.project.common_domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminCreateRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Surname cannot be empty")
    private String surname;

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    private String password;


}
