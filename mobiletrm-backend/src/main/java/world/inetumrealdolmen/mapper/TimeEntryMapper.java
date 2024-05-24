package world.inetumrealdolmen.mapper;

import world.inetumrealdolmen.domain.TimeRegistration;
import world.inetumrealdolmen.dto.TimeEntryDto;

/**
 * Provides static methods to map the start and end time of a {@link TimeRegistration}.
 */
public class TimeEntryMapper {
  /**
   * Maps a TimeRegistration object to a TimeEntryDto object.
   *
   * @param timeRegistration The Tag object to be mapped.
   * @return A TimeEntryDto object mapped from the given Time Registration.
   */
  public static TimeEntryDto toDto(TimeRegistration timeRegistration) {
    return new TimeEntryDto(
        timeRegistration.id,
        timeRegistration.startTime.toLocalTime(),
        timeRegistration.endTime.toLocalTime()
    );
  }
}