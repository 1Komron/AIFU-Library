package aifu.project.common_domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class AdminActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Librarian librarian;

    private String action;

    private String studentName;
    private String studentSurname;

    private String bookTitle;
    private String bookAuthor;
    private String bookInventoryNumber;

    private LocalDateTime createdAt;
    private LocalDate createdDate;
}
