package aifu.project.common_domain.dto.statistic_dto;

import java.time.LocalDate;

public record BookingsPerDayDTO(LocalDate date, int taken ,int returned, int returnedLate) {
}
