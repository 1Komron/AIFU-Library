package aifu.project.common_domain.dto.notification_dto;

import aifu.project.common_domain.entity.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dtoType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationExtendShortDTO.class, name = "EXTEND"),
        @JsonSubTypes.Type(value = NotificationWarningShortDTO.class, name = "WARNING")
})
public interface NotificationShortDTO {
    Long id();
    NotificationType notificationType();
    String date();
    boolean isRead();
}
