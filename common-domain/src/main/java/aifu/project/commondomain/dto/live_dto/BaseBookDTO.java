package aifu.project.commondomain.dto.live_dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseBookDTO {

    private Integer id;

    private String author;

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

    private Integer categoryId;

    private List<BookCopyDTO> copies = new ArrayList<>();


}