package aifu.project.commondomain.payload;

public record WebDTO(
        String name,
        String surname,
        String phone,
        String email,
        String password
) {
}
