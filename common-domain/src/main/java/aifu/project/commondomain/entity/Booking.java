package aifu.project.commondomain.entity;

import aifu.project.commondomain.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToOne
    private BookCopy book;

    @CreationTimestamp
    private LocalDateTime givenAt;

    @CreationTimestamp
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void setDueDate() {
        this.givenAt = LocalDateTime.now();
        this.dueDate = this.givenAt.plusDays(4);
    }
}
