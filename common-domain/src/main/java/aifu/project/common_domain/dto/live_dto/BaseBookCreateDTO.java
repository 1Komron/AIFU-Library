package aifu.project.common_domain.dto.live_dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record BaseBookCreateDTO(

        @NotNull Integer categoryId,

        @NotBlank String author,

        @NotBlank String title,

        String series,

        String titleDetails,

        @JsonFormat(pattern = "yyyy")
        @NotNull Integer publicationYear,

        @NotBlank String publisher,

        @NotBlank String publicationCity,

        @NotBlank String isbn,

        @Min(1)
        @NotNull Integer pageCount,

        @NotBlank String language,

        @NotBlank String udc
) {

}

