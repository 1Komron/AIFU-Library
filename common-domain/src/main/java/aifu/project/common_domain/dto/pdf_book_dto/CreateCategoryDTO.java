package aifu.project.common_domain.dto.pdf_book_dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class CreateCategoryDTO {

    @NotBlank
    private String name;

}
