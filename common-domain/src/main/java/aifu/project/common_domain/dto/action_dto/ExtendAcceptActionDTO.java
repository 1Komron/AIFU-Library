package aifu.project.common_domain.dto.action_dto;

public record ExtendAcceptActionDTO(Long notificationId, Integer extendDays) {
    public ExtendAcceptActionDTO {
        if (notificationId == null || notificationId <= 0) {
            throw new IllegalArgumentException("Notification ID topilmadi. ID " + notificationId);
        }
        if (extendDays == null || extendDays <= 0) {
            throw new IllegalArgumentException("Bron uzaytirish muddati notogir kiritildi. Extend days: " + extendDays);
        }
    }
}
