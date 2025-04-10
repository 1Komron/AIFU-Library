package aifu.project.libraryweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookCopyDTO {
    private Integer id;
    private String inventoryNumber;
    private String shelfLocation;
    private String notes;
    private BaseBookDTO book;

}
