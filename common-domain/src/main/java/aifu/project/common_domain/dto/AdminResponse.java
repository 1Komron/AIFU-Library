package aifu.project.common_domain.dto;

import aifu.project.common_domain.entity.Librarian;
import lombok.Builder;
import lombok.Data;

@Builder
public record AdminResponse(Long id,
                            String name,
                            String surname,
                            String email,
                            String role,
                            String imageUrl,
                            Boolean isActive) {

    public static AdminResponse toDto(Librarian librarian) {
        return new AdminResponse(
                librarian.getId(),
                librarian.getName(),
                librarian.getSurname(),
                librarian.getEmail(),
                librarian.getRole().name(),
                librarian.getImageUrl(),
                librarian.isActive()
        );
    }

}
