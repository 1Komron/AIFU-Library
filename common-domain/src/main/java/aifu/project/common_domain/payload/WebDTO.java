package aifu.project.common_domain.payload;

public record WebDTO(
        String name,
        String surname,
        String email,
        String password
) {
}
