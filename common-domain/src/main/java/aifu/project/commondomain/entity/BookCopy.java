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

    @ManyToOne
    @JoinColumn(name = "base_book_id")
    private BaseBook book;

}
