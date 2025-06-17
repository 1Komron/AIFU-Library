package aifu.project.common_domain.dto.live_dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class BaseBookCreateDTO {

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
