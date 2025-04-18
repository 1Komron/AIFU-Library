package aifu.project.commondomain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private BookCopy book;
}
