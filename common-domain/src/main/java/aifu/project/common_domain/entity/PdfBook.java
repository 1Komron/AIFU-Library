package aifu.project.common_domain.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
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

    private String description;

    @Builder.Default
    private LocalDate localDate = LocalDate.now();

}