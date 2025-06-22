package aifu.project.common_domain.entity.enums;


public enum Status {
    APPROVED,
    OVERDUE,
    WAITING_APPROVAL;

    public static Status getStatus(String status) {
       Status[] values = Status.values();
        for (Status value : values)
            if (value.name().equalsIgnoreCase(status))
                return value;

        return null;
    }
}
