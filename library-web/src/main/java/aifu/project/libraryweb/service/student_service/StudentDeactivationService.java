package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.student_dto.DeactivationResultDTO;
import aifu.project.common_domain.dto.student_dto.FileDownloadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface StudentDeactivationService {

   DeactivationResultDTO startDeactivationProcess(MultipartFile file) throws IOException;
   FileDownloadDTO getDebtorsReport(UUID jobId);
   FileDownloadDTO getNotFoundReport(UUID jobId);

}