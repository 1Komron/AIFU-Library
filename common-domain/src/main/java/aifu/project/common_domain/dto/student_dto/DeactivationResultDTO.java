package aifu.project.common_domain.dto.student_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeactivationResultDTO {
    private UUID jobId;
    private int successCount;
    private int debtorCount;
    private int notFoundCount;
    private String downloadDebtorsReportUrl;
    private String downloadNotFoundReportUrl;
}