package aifu.project.common_domain.dto.pdf_book_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponseDTO {

    private String url;

    private Double sizeMB;

}
