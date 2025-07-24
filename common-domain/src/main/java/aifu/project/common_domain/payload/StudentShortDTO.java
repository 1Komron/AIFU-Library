package aifu.project.common_domain.payload;

public record StudentShortDTO(
        Long id,
        String name,
        String surname,
        String phone,
        boolean status
) {
}
