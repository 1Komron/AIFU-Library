package aifu.project.libraryweb.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BaseBookDTO {
    private String auther;
    private String title;
    private String series;
    private String titleDetails;
    private int publicationYear;
    private String publisher;
    private String publicationCity;
    private String isbn;
    private int pageCount;
    private String language;
    private Double price;
    private String udc;
}
