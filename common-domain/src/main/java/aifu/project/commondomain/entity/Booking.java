package aifu.project.commondomain.entity;

import aifu.project.commondomain.entity.enums.Status;
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
    private User user;

    @OneToOne
    private BookCopy book;

    private LocalDate givenAt;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void initializeDates() {
        this.givenAt = LocalDate.now();
        this.dueDate = this.givenAt.plusDays(4);
    }

}
