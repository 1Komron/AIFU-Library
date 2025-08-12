package aifu.project.common_domain.dto.histroy_dto;

import aifu.project.common_domain.entity.Librarian;

public record HistoryAdminDTO(String name, String surname, String email) {
    public static HistoryAdminDTO toDTO(Librarian librarian) {
        return new HistoryAdminDTO(
                librarian.getName(),
                librarian.getSurname(),
                librarian.getEmail()
        );
    }
}
