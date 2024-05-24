package world.inetumrealdolmen.dto;

import java.time.LocalTime;
import world.inetumrealdolmen.domain.TimeRegistration;

/**
 * A Data Transfer Objects (DTO) for start and end time of a {@link TimeRegistration}.
 */
public record TimeEntryDto(
    Long id,
    LocalTime startTime,
    LocalTime endTime
) {
}
