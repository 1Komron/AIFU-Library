package aifu.project.commondomain.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class PdfBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double size;

    private String author;

    private String title;

    private int publicationYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String pdfUrl;

    private String imageUrl;

    private String isbn;

    private Integer pageCount;

    private String publisher;

    private String language;

    private String script;

    @Builder.Default
    private LocalDate localDate = LocalDate.now();

}