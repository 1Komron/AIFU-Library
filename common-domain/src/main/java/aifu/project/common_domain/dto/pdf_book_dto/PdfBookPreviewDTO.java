package aifu.project.common_domain.dto.pdf_book_dto;

import aifu.project.common_domain.entity.Category;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfBookPreviewDTO {

    private Integer id;

    private String author;

    private String title;

    private String imageUrl;

    private CategoryPreviewDTO categoryPreviewDTO;


}
