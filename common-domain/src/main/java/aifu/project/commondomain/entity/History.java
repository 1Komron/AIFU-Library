package aifu.project.commondomain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne
    private BookCopy book;

    private LocalDate givenAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;
}
