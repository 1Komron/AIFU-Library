package aifu.project.common_domain.dto.pdf_book_dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfBookUpdateDTO {

    private String author;

    private String title;

    private Integer publicationYear;

    private Integer categoryId;

    private String pdfUrl;

    private String imageUrl;

    private String isbn;

    private Integer pageCount;

    private String publisher;

    private String language;

    private String script;

    private Double size;

}
