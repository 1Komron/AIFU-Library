package aifu.project.common_domain.dto.action_dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExtendAcceptActionDTO(@NotNull Long notificationId, @Min(1) Integer extendDays) {
}
