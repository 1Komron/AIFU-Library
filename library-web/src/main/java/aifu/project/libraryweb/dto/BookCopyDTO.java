package aifu.project.libraryweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class BookCopyDTO {
    private String inventoryNumber;
    private String shelfLocation;
    private String notes;
    private BaseBookDTO baseBook;

}
