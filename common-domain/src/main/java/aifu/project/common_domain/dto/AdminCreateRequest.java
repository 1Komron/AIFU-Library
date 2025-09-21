package aifu.project.common_domain.dto;

import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminCreateRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Surname cannot be empty")
    private String surname;

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    private String password;


    public static Librarian toEntity(AdminCreateRequest request, String password) {
        Librarian admin = new Librarian();
        admin.setName(request.getName());
        admin.setSurname(request.getSurname());
        admin.setEmail(request.getEmail());
        admin.setPassword(password);
        admin.setRole(Role.ADMIN);
        admin.setDeleted(false);
        admin.setActive(false);

        return admin;
    }

}
