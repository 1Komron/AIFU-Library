package aifu.project.common_domain.dto.notification_dto;

import aifu.project.common_domain.dto.student_dto.StudentDTO;

public record NotificationExtendDetailDTO(
        Long id,
        StudentDTO student,
        BookDTO book
) {
}
