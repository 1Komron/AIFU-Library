package aifu.project.commondomain.entity;

import aifu.project.commondomain.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requestId;
    private String userName;
    private String userSurname;
    private String phone;
    private NotificationType notificationType;
    private LocalDateTime notificationTime;
    private boolean isRead = false;
}
