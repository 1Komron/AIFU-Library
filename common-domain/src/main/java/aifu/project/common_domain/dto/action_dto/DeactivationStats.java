package aifu.project.common_domain.dto.action_dto;

import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeactivationStats {
    private int successCount;
    private List<DebtorInfoDTO> debtors;
    private List<ImportErrorDTO> notFoundRecords; // "Topilmaganlar" uchun ham to'liq ma'lumot

    // Har bir hisobot uchun alohida fayl
    private String debtorsReportFileName;
    private String debtorsReportFileBase64;
    private String notFoundReportFileName;
    private String notFoundReportFileBase64;

    public DeactivationStats(int successCount, List<DebtorInfoDTO> debtors, List<ImportErrorDTO> notFoundRecords) {
        this.successCount = successCount;
        this.debtors = debtors;
        this.notFoundRecords = notFoundRecords;
    }

    @JsonIgnore
    public String generateResponseMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d ta talaba muvaffaqiyatli deaktivatsiya qilindi.", successCount));

        if (debtors != null && !debtors.isEmpty()) {
            sb.append(String.format(" %d ta talaba qarzdorligi sababli o'chirilmadi.", debtors.size()));
            if (debtorsReportFileName != null) sb.append(" Qarzdorlar hisobotini yuklab oling.");
        }

        if (notFoundRecords != null && !notFoundRecords.isEmpty()) {
            sb.append(String.format(" %d ta talaba bazadan topilmadi.", notFoundRecords.size()));
            if (notFoundReportFileName != null) sb.append(" Topilmaganlar hisobotini yuklab oling.");
        }

        return sb.toString();
    }
}