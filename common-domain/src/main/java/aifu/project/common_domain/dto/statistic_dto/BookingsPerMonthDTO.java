package aifu.project.common_domain.dto.statistic_dto;

public record BookingsPerMonthDTO(int month, int taken, int returned, int returnedLate) {}

