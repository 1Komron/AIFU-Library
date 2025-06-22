package aifu.project.common_domain.entity.enums;

public enum NotificationType {
    BORROW, RETURN, EXTEND, REGISTER;

    public static NotificationType getNotification(String type) {
        for (NotificationType value : NotificationType.values()) {
            if (value.name().equalsIgnoreCase(type))
                return value;
        }
        return null;
    }
}
