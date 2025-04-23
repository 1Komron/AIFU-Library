package aifu.project.commondomain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    private LocalDateTime givenAt;
    private LocalDateTime dueDate;
    private LocalDateTime returnedAt;
}
