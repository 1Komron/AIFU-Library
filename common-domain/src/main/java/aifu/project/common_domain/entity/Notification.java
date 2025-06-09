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

    private Long requestId;
    private String userName;
    private String userSurname;
    private String phone;
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    @Enumerated(EnumType.STRING)
    private RequestType requestType;
    private LocalDateTime notificationTime;
    private boolean isRead = false;

    public Notification(User user, Long requestId, NotificationType type, RequestType requestType) {
        this.requestId = requestId;
        this.userName = user.getName();
        this.userSurname = user.getSurname();
        this.phone = user.getPhone();
        this.notificationType = type;
        this.requestType = requestType;
        this.notificationTime = LocalDateTime.now();
    }
}
