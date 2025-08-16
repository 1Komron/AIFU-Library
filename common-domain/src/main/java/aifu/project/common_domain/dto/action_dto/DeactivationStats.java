package aifu.project.common_domain.dto.action_dto;

import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class DeactivationStats {
    private int successCount;
    private List<DebtorInfoDTO> debtors; // QARZDORLAR RO'YXATI
    private List<String> notFoundOrOtherErrors; // Topilmaganlar va boshqa xatolar

    public DeactivationStats(int successCount, List<DebtorInfoDTO> debtors, List<String> notFoundOrOtherErrors) {
        this.successCount = successCount;
        this.debtors = debtors;
        this.notFoundOrOtherErrors = notFoundOrOtherErrors;
    }
}