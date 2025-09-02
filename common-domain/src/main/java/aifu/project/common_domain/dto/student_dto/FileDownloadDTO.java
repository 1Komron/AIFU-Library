package aifu.project.common_domain.dto.student_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadDTO {
    private String fileName;
    private ByteArrayResource resource;
}
