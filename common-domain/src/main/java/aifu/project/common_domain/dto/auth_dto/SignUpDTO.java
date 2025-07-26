package aifu.project.common_domain.dto.auth_dto;

public record SignUpDTO(
        String name,
        String surname,
        String email,
        String password
) {
}
