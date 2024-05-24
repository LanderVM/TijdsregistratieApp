package world.inetumrealdolmen.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Set;
import world.inetumrealdolmen.domain.TimeRegistration;
import world.inetumrealdolmen.validation.NullOrNotBlank;
import world.inetumrealdolmen.validation.NullOrPositive;

/**
 * A collection of Data Transfer Objects (DTO)
 * for {@link TimeRegistration} entities to be sent to clients.
 * Use the static subclasses' constructors to create DTO's.
 */
public class TimeRegistrationDto {

  /**
   * Represents an index view of a time registration.
   */
  public static class Index {
    public long id;
    public String description;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public TaskDto.Index assignedTask;
    public ProjectDto.Name assignedProject;
    public Set<TagDto.Index> tags;
  }

  /**
   * Represents a create request of a time registration.
   */
  public static class Create {
    @NullOrNotBlank
    public String description;
    @NotNull
    public LocalDateTime startTime;
    @NotNull
    public LocalDateTime endTime;
    @Positive
    public Long assignedProjectId;
    @NullOrPositive
    public Long assignedTaskId;
    public Set<TagDto.@Valid Index> tags;
  }

  /**
   * Represents an update request of a time registration.
   */
  public static class Update extends Create {
    @Positive
    public Long id;
  }

  /**
   * Represents the response upon creating a new time registration.
   *
   * @param id                the ID of the newly registered time registration.
   * @param description       the description of the newly registered time registration.
   * @param startTime         the startTime of the newly registered time registration.
   * @param endTime           the endTime of the newly registered time registration.
   * @param assignedProjectId the assigned project's id of the newly registered time registration.
   * @param assignedTaskId    the assigned task's id of the newly registered time registration.
   * @param tags              the assigned tags of the newly registered time registration.
   */
  public record CreateResponse(
      Long id,
      String description,
      LocalDateTime startTime,
      LocalDateTime endTime,
      Long assignedProjectId,
      Long assignedTaskId,
      Set<TagDto.Index> tags
  ) {
  }
}
