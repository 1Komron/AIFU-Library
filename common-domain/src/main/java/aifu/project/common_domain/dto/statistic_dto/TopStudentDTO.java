package aifu.project.common_domain.dto.statistic_dto;

public record TopStudentDTO(
        String name,
        String surname,
        String degree,
        String faculty,
        int usageCount
) {
}
