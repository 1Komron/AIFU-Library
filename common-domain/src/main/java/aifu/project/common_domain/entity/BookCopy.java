package aifu.project.common_domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "book")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)

    private String inventoryNumber;

    private String shelfLocation;

    private String notes;

    @Builder.Default
    private boolean isTaken = false;

    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_book_id")
    private BaseBook book;
}
