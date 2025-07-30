package aifu.project.common_domain.entity;

import aifu.project.common_domain.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private BookCopy bookCopy;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private LocalDateTime notificationTime;
    private boolean isRead = false;

    public Notification(Student student, BookCopy bookCopy, NotificationType type) {
        this.student = student;
        this.bookCopy = bookCopy;
        this.notificationType = type;
        this.notificationTime = LocalDateTime.now();
    }
}
