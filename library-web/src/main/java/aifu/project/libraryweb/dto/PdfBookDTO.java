package aifu.project.libraryweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfBookDTO {

    private String author;

    private String title;

    private int publicationYear;

    private String pdfUrl;

    private String imageUrl;

    private LocalDate localDate;


}
