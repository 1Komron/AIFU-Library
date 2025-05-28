package aifu.project.commondomain.dto.pdf_book_dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PdfBookResponseDTO {

    private Integer id;

    private Double size;

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

    private LocalDate localDate;

}
