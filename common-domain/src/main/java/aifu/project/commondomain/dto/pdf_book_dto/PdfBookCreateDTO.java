package aifu.project.commondomain.dto.pdf_book_dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfBookCreateDTO {

    private String author;

    private String title;

    private Integer publicationYear;

    private String pdfUrl;

    private String imageUrl;

    private String script;

    private String language;

    private String publisher;

    private Integer pageCount;

    private String isbn;

    private Double size;


}