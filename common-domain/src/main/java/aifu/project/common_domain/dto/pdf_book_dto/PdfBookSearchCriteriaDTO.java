package aifu.project.common_domain.dto.pdf_book_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfBookSearchCriteriaDTO {

    @Schema(description = "Qidirilayotgan qiymat (title yoki author)", example = "Java")
    private String value;

    @Schema(description = "Qaysi maydon bo'yicha qidirish: 'title' yoki 'author'", example = "title")
    private String field;

    @Schema(description = "Sahifa raqami", example = "1")
    private int pageNumber = 1;

    @Schema(description = "sahifa raqami",example = "10")
    private int size = 10;

    @Schema(description = "siralash yonlashi: id yoki name",example = "id")
    private String sortBy = "id";

    @Schema(description = "saralash yunalishi: asc yoki desc",example = "asc")
    private String sortDr = "asc";


}
