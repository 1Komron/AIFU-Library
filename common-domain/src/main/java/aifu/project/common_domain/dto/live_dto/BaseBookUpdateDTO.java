package aifu.project.common_domain.dto.live_dto;

import lombok.Data;

@Data
public class BaseBookUpdateDTO {

    private String author;

    private String title;

    private String series;

    private String titleDetails;

    private Integer publicationYear;

    private String publisher;

    private String publicationCity;

    private String isbn;

    private Integer pageCount;

    private String language;

    private String udc;

    private Integer categoryId;

}
