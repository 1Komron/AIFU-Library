package aifu.project.common_domain.dto.student_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResultDTO {
    private UUID jobId;
    private int successCount;
    private int errorCount;
    private String downloadReportUrl;
    // Frontend uchun tayyor URL
}