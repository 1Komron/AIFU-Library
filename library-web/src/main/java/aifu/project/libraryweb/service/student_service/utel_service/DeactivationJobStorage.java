package aifu.project.libraryweb.service.student_service.utel_service;

import aifu.project.common_domain.dto.student_dto.DebtorInfoDTO;
import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeactivationJobStorage {

    @Data
    @AllArgsConstructor
    public static class ReportData {
        private List<DebtorInfoDTO> debtors;
        private List<ImportErrorDTO> notFoundRecords;
    }

    private final Map<UUID, ReportData> storage = new ConcurrentHashMap<>();

    public void saveReports(UUID jobId, List<DebtorInfoDTO> debtors, List<ImportErrorDTO> notFoundRecords) {
        storage.put(jobId, new ReportData(debtors, notFoundRecords));
    }

    public ReportData getReports(UUID jobId) {
        return storage.get(jobId);
    }
}