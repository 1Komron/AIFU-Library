package aifu.project.common_domain.dto.live_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BookCopyResponseDTO {

    private Integer id;

    private String inventoryNumber;

    private String shelfLocation;

    private String notes;

    private Integer baseBookId;
}
