package aifu.project.common_domain.dto.live_dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BaseBookCategoryCreateDTO {

    @NotBlank
    private String name;
}
