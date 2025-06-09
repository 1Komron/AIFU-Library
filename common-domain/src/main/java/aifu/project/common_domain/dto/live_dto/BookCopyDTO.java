package aifu.project.common_domain.dto.live_dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyDTO {
    private Integer id;

    private String inventoryNumber;

    private String shelfLocation;

    private String notes;

    private Integer baseBookId;
}