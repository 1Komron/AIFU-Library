package aifu.project.commondomain.dto;

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