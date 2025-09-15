package aifu.project.common_domain.dto.live_dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;

public record BaseBookUpdateDTO(
        Integer categoryId,

        String author,

        String title,

        String series,

        String titleDetails,

        @JsonFormat(pattern = "yyyy")
        Integer publicationYear,

        String publisher,

        String publicationCity,

        String isbn,

        @Min(1)
        Integer pageCount,

        String language,

        String udc
) {

}
