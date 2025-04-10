package aifu.project.libraryweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BaseBookDTO {
    private Integer id;
    private String auther;
    private String title;
    private String series;
    private String titleDetails;
    private Integer publicationYear;
    private String publisher;
    private String publicationCity;
    private String isbn;
    private Integer pageCount;
    private String language;
    private Double price;
    private String udc;
}
