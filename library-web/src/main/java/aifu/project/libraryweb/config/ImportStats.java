package aifu.project.libraryweb.config;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/*@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportStats {
    private int successCount;
    private List<ImportErrorDTO> failedRecords; // Xatolikka uchragan yozuvlar ro'yxati

    public ImportStats(int successCount, List<ImportErrorDTO> failedRecords) {
        this.successCount = successCount;
        this.failedRecords = failedRecords;
    }
 ;
}*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportStats {
    private int successCount;
    private List<ImportErrorDTO> failedRecords;
    private String reportFileName;
    private String reportFileBase64;

    public ImportStats(int successCount, List<ImportErrorDTO> failedRecords) {
        this.successCount = successCount;
        this.failedRecords = failedRecords;
    }
}