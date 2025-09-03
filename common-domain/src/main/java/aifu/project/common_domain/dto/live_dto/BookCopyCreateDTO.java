package aifu.project.common_domain.dto.live_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookCopyCreateDTO(

        @NotBlank String inventoryNumber,

        String shelfLocation,

        String notes,

        @NotNull Integer baseBookId,

        String epc) {

}
