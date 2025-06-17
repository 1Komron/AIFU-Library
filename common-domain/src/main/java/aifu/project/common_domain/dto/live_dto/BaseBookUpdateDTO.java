package aifu.project.common_domain.dto.live_dto;

import lombok.Data;

@Data
public class BaseBookUpdateDTO {

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

    private String udc;

    private Integer categoryId;

}
