package aifu.project.libraryweb.service.student_service.utel_service;

import aifu.project.common_domain.dto.student_dto.ImportErrorDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImportJobStorage {
    private final Map<UUID, List<ImportErrorDTO>> errorStorage = new ConcurrentHashMap<>();

    public void saveErrors(UUID jobId, List<ImportErrorDTO> errors) {
        if (errors != null && !errors.isEmpty()) {
            errorStorage.put(jobId, errors);
        }
    }

    public List<ImportErrorDTO> getErrors(UUID jobId) {
        List<ImportErrorDTO> errors = errorStorage.get(jobId);
        return errors != null ? errors : Collections.emptyList();
    }
}
