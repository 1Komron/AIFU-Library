package aifu.project.common_domain.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(@NotBlank String name) {
}
