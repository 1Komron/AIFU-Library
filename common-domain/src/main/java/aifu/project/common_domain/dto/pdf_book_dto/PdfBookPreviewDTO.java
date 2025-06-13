package aifu.project.common_domain.dto.pdf_book_dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfBookPreviewDTO {

    private String author;

    private String title;

    private String imageUrl;

    private Integer categoryId;
}
