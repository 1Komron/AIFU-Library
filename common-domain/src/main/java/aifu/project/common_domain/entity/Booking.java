package aifu.project.common_domain.entity;

import aifu.project.common_domain.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @OneToOne
    private BookCopy book;

    private LocalDate givenAt;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Librarian issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Librarian extendedBy;

    private LocalDate extendedAt;

}
