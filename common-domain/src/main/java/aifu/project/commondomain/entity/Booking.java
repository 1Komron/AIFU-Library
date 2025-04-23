package aifu.project.commondomain.entity;

import aifu.project.commondomain.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    private LocalDateTime givenAt;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void initializeDates() {
        this.givenAt = LocalDateTime.now();
        this.dueDate = this.givenAt.plusDays(4);
    }

}
