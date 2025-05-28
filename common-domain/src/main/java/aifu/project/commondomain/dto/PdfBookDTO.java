package aifu.project.commondomain.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfBookDTO {

    private Integer id;
    private String author;
    private String title;
    private int publicationYear;
    private String pdfUrl;
    private String imageUrl;
    private LocalDate localDate;
    private String script;
    private String language;
    private String publisher;
    private Integer pageCount;
    private String isbn;
    private Double size;


}