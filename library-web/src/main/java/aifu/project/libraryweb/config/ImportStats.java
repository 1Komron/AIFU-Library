package aifu.project.libraryweb.config;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

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

    @JsonIgnore
    public String generateResponseMessage() {
        if (successCount == 0 && (failedRecords == null || failedRecords.isEmpty())) {
            return "Import qilinadigan yaroqli ma'lumotlar topilmadi.";
        }
        String successPart = String.format("%d ta talaba muvaffaqiyatli import qilindi.", successCount);

        if (failedRecords != null && !failedRecords.isEmpty()) {
            String failurePart = String.format(" %d ta yozuvda xatolik aniqlandi.", failedRecords.size());
            String reportPart = (reportFileName != null) ? " Tafsilotlar uchun generatsiya qilingan hisobot faylini yuklab oling." : "";
            return successPart + failurePart + reportPart;
        }
        return successPart;
      }
   }