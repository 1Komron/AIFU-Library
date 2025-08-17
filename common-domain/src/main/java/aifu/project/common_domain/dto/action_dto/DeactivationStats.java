package aifu.project.common_domain.dto.action_dto;

import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeactivationStats {
    private int successCount;
    private List<DebtorInfoDTO> debtors;
    private List<String> notFoundOrOtherErrors;

    private String reportFileName;
    private String reportFileBase64;

    public DeactivationStats(int successCount, List<DebtorInfoDTO> debtors, List<String> notFoundOrOtherErrors) {
        this.successCount = successCount;
        this.debtors = debtors;
        this.notFoundOrOtherErrors = notFoundOrOtherErrors;
    }
}
