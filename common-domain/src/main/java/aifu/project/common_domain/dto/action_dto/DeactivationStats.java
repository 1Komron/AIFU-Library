package aifu.project.common_domain.dto.action_dto;

import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    public String generateResponseMessage() {
        if (successCount == 0 && (debtors == null || debtors.isEmpty()) && (notFoundOrOtherErrors == null || notFoundOrOtherErrors.isEmpty())) {
            return "Faylda qayta ishlanadigan ma'lumot topilmadi.";
        }
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(String.format("%d ta talaba muvaffaqiyatli deaktivatsiya qilindi.", successCount));
        if (debtors != null && !debtors.isEmpty()) {
            messageBuilder.append(String.format(" %d ta talaba qarzdorligi sababli o'chirilmadi.", debtors.size()));
            if (reportFileName != null) {
                messageBuilder.append(" Tafsilotlar uchun generatsiya qilingan hisobot faylini yuklab oling.");
            }
        }
        if (notFoundOrOtherErrors != null && !notFoundOrOtherErrors.isEmpty()) {
            messageBuilder.append(String.format(" %d ta yozuvda boshqa xatoliklar (masalan, bazada topilmadi) aniqlandi.", notFoundOrOtherErrors.size()));
        }

        return messageBuilder.toString();
    }
}
