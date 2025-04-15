package aifu.project.commondomain.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String inventoryNumber;
    private String shelfLocation;
    private String notes;
    private boolean isTaken = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_book_id")
    private BaseBook book;

}
