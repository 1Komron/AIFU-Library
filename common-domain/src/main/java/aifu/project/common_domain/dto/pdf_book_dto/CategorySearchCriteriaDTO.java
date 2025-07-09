package aifu.project.common_domain.dto.pdf_book_dto;


import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CategorySearchCriteriaDTO {

    private Integer id;

    private String name;

    private int pageNumber = 1;

    private int size = 10;

    private String sortBy = "id";

    private String sortDr = "asc";


}
