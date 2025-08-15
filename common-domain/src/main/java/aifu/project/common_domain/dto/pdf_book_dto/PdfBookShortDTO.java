package aifu.project.common_domain.dto.pdf_book_dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfBookShortDTO {

    private Integer id;

    private String isbn;

    private String author;

    private String title;

    private String imageUrl;

    private CategoryPreviewDTO categoryPreviewDTO;
}
