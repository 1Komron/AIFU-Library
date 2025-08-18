package aifu.project.common_domain.dto.student_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportErrorDTO {
    private String name;
    private String surname;
    private String degree;
    private String faculty;
    private String errorReason;

}
