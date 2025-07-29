package aifu.project.common_domain.entity;

import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.entity.enums.RequestType;
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

    private String userName;
    private String userSurname;
    private String phone;
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    @Enumerated(EnumType.STRING)
    private RequestType requestType;
    private LocalDateTime notificationTime;
    private boolean isRead = false;

    public Notification(Student student, NotificationType type, RequestType requestType) {
        this.userName = student.getName();
        this.userSurname = student.getSurname();
        this.notificationType = type;
        this.requestType = requestType;
        this.notificationTime = LocalDateTime.now();
    }
}
