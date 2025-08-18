package aifu.project.common_domain.dto.student_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportErrorDTO {

    private String name;
    private String surname;
    private String degree;
    private String faculty;
    private LocalDate admissionTime;
    private LocalDate graduationTime;
    private String errorReason;

}
