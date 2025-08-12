package aifu.project.common_domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class BaseBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String author;

    private String title;

    private String series;

    private String titleDetails;

    private int publicationYear;

    private String publisher;

    private String publicationCity;

    private String isbn;

    private int pageCount;

    private String language;

    private String udc;

    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private BaseBookCategory category;

    @OneToMany(mappedBy = "book", orphanRemoval = true)
    @Builder.Default
    private List<BookCopy> copies = new ArrayList<>();

}


