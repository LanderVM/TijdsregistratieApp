package world.inetumrealdolmen.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import world.inetumrealdolmen.domain.Project;

/**
 * A collection of Data Transfer Objects (DTO) for {@link Project} entities to be sent to clients.
 * Use the static subclasses' constructors to create DTO's.
 */
public class ProjectDto {

  /**
   * Represents the body of a PUT operation to mark a project as (un)favorited.
   *
   * @param isFavorite whether the project should be favorited or unfavorited.
   */
  public record UpdateFavorite(
      @NotNull Boolean isFavorite
  ) {
  }

  /**
   * Represents a smaller index view of a project, containing only its name.
   *
   * @param id   the ID of the project.
   * @param name the name of the project.
   */
  public record Name(
      Long id,
      String name
  ) {
  }

  /**
   * Represents an index view of a project, containing information for projects overview.
   */
  // TODO record
  public static class Index {

    /**
     * The ID of the project.
     */
    public long id;

    /**
     * The name of the project.
     */
    public String name;

    /**
     * The fixed deadline of the project.
     * Will be null if the project has a total amount of work minutes instead.
     */
    public LocalDate endDate;

    /**
     * The total amount of work minutes ordered for the project.
     * Will be null if the project has a fixed deadline instead.
     * Total amount of work minutes left can be calculated using the project's tasks field.
     */
    public Long workMinutesLeft;

    /**
     * Whether the project is in the user's list of favorited projects.
     */
    public Boolean isFavorite;

    /**
     * When the project was last (un)favorited by the user.
     */
    public LocalDateTime whenFavorited;
  }

  /**
   * Represents a detailed view of a project,
   * containing all the project's details as well as the user's time registrations for this project.
   */
  public static class Details extends Index {

    /**
     * The description of the project.
     */
    public String description;

    /**
     * The customer associated with the project.
     */
    public CustomerDto.Index customer;

    /**
     * The start date of the project.
     */
    public LocalDate startDate;

    /**
     * The set of tasks associated with the project.
     */
    public List<TaskDto.Index> tasks;

    /**
     * The set of time-registrations associated with the project.
     */
    public List<TimeRegistrationDto.Index> timeRegistration;
  }

  /**
   * Represents a detailed view of the tasks for each project.
   */
  public static class Tasks {

    public long id;
    public String name;
    public List<TaskDto.Index> tasks;

    /**
     * Default constructor for Jackson serializing.
     * Protected to prevent instantiation without parameters.
     */
    protected Tasks() {
    }

    /**
     * Constructor to convert a Project object into a DTO with tasks.
     *
     * @param project The Project object to convert.
     */
    public Tasks(Project project) {
      id = project.id;
      name = project.name;
      tasks = project.tasks.stream().map(TaskDto.Index::new).toList();
    }
  }
}