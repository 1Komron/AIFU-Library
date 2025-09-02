package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.FileDownloadDTO;
import aifu.project.common_domain.dto.student_dto.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

public interface StudentExcelImportService {

      ImportResultDTO importStudents(MultipartFile file);

      FileDownloadDTO getErrorReport(UUID jobId);

      FileDownloadDTO getTemplateFile();
}
