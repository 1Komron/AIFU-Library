package aifu.project.common_domain.payload;

public record UserShortDTO(
        Long id,
        String name,
        String surname,
        String cardNumber,
        boolean status
) {
}
