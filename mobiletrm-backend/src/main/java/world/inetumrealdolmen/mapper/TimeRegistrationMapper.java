package world.inetumrealdolmen.mapper;

import java.time.temporal.ChronoUnit;
import java.util.List;
import world.inetumrealdolmen.domain.TimeRegistration;
import world.inetumrealdolmen.dto.TimeRegistrationDto;

/**
 * Provides static methods to map between {@link TimeRegistration} objects
 * and {@link TimeRegistrationDto} objects.
 */
public class TimeRegistrationMapper {


  /**
   * Maps a TimeRegistration object to a TimeRegistrationDto.Index object.
   *
   * @param timeRegistration The TimeRegistration object to be mapped.
   * @return A TimeRegistrationDto.Index object mapped from the given TimeRegistration.
   */
  public static TimeRegistrationDto.Index toDto(TimeRegistration timeRegistration) {
    var dto = new TimeRegistrationDto.Index();
    dto.id = timeRegistration.id;
    dto.description = timeRegistration.description;
    dto.startTime = timeRegistration.startTime.truncatedTo(ChronoUnit.MINUTES);
    dto.endTime = timeRegistration.endTime.truncatedTo(ChronoUnit.MINUTES);
    dto.assignedProject = ProjectMapper.toNameDto(timeRegistration.assignedProject);
    dto.assignedTask = TaskMapper.toDto(timeRegistration.assignedTask);
    dto.tags = TagMapper.toDto(timeRegistration.tags);
    return dto;
  }

  /**
   * Maps a list of TimeRegistration objects to TimeRegistrationDto.Index objects.
   *
   * @param timeRegistrations The TimeRegistration list to be mapped.
   * @return A list of TimeRegistrationDto.Index object mapped from the given TimeRegistrations.
   */
  public static List<TimeRegistrationDto.Index> toDto(List<TimeRegistration> timeRegistrations) {
    return timeRegistrations.stream().map(TimeRegistrationMapper::toDto).toList();
  }

  /**
   * Maps a list of TimeRegistration object to a list of TimeRegistrationDto.CreateResponse object.
   *
   * @param timeRegistration The list of TimeRegistrations to be mapped.
   * @return A list of mapped TimeRegistrationDto.CreateResponse objects.
   */
  public static TimeRegistrationDto.CreateResponse toCreateResponse(
      TimeRegistration timeRegistration) {
    return
        new TimeRegistrationDto.CreateResponse(
            timeRegistration.id,
            timeRegistration.description,
            timeRegistration.startTime,
            timeRegistration.endTime,
            timeRegistration.assignedProject != null ? timeRegistration.assignedProject.id : null,
            timeRegistration.assignedTask != null ? timeRegistration.assignedTask.id : null,
            TagMapper.toDto(timeRegistration.tags)
        );
  }

  /**
   * Maps a TimeRegistrationDto.Index object to a TimeRegistration object.
   *
   * @param dto The TimeRegistrationDto.Index object to be mapped.
   * @return A TimeRegistration object mapped from the given TimeRegistrationDto.Index object.
   */
  public static TimeRegistration fromDto(TimeRegistrationDto.Create dto) {
    var timeRegistration = new TimeRegistration();
    timeRegistration.description = dto.description;
    timeRegistration.startTime = dto.startTime;
    timeRegistration.endTime = dto.endTime;
    return timeRegistration;
  }

}
