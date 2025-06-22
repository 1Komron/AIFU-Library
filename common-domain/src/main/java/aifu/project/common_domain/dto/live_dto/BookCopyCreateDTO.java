package aifu.project.common_domain.dto.live_dto;

import lombok.Data;

@Data
public class BookCopyCreateDTO {

    private String inventoryNumber;

    private String shelfLocation;

    private String notes;

    private Integer baseBookId;

}
