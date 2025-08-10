package aifu.project.common_domain.dto.action_dto;

import java.util.List;

public record DeactivationStats (
    int successfullyDeactivated,
    List<String>  failedDeactivations
){}
