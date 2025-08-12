package aifu.project.common_domain.dto.histroy_dto;

import aifu.project.common_domain.dto.student_dto.StudentSummaryDTO;
import aifu.project.common_domain.entity.History;

import java.time.LocalDate;

public record HistorySummaryDTO(
        Long id,
        StudentSummaryDTO student,
        HistoryBookDTO book,
        HistoryAdminDTO issuedBy,
        HistoryAdminDTO returnedBy,
        LocalDate givenAt,
        LocalDate dueDate,
        LocalDate returnedAt
) {
    public static HistorySummaryDTO toDTO(History history) {
        return new HistorySummaryDTO(
                history.getId(),
                StudentSummaryDTO.toDTO(history.getUser()),
                HistoryBookDTO.toDTO(history.getBook()),
                HistoryAdminDTO.toDTO(history.getIssuedBy()),
                HistoryAdminDTO.toDTO(history.getReturnedBy()),
                history.getGivenAt(),
                history.getDueDate(),
                history.getReturnedAt()
        );
    }
}
