package aifu.project.common_domain.payload;

import aifu.project.common_domain.entity.enums.NotificationType;
import aifu.project.common_domain.entity.enums.RequestType;

public record BookingRequestDTO(
        BotUserDTO user,
        Integer bookId,
        String author,
        String title,
        String isbn,
        String inventoryNumber,
        RequestType requestType,
        NotificationType notificationType
) {

}
