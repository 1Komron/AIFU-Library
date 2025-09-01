package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.ImportResultDTO;
import aifu.project.libraryweb.config.ImportStats;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public interface StudentExcelImportService {
      ImportResultDTO importStudents(MultipartFile file) throws IOException;
      Map<String, Object> getErrorReport(UUID jobId);
}
