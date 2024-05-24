package world.inetumrealdolmen.mapper;

import java.util.List;
import world.inetumrealdolmen.domain.Project;
import world.inetumrealdolmen.domain.TimeRegistration;
import world.inetumrealdolmen.dto.CustomerDto;
import world.inetumrealdolmen.dto.ProjectDto;
import world.inetumrealdolmen.dto.TaskDto;

/**
 * Provides static methods to map between {@link Project} objects and {@link ProjectDto} objects.
 */
public class ProjectMapper {

  /**
   * Maps a Project object to a ProjectDto.Index object.
   *
   * @param project The Project object to be mapped.
   * @param userId The user's id to filter on user-specific details.
   * @return A Project.Index object
   */
  public static ProjectDto.Index toDto(Project project, Long userId) {
    var dto = new ProjectDto.Index();
    setIndexInformation(project, dto, userId);
    return dto;
  }

  /**
   * Maps a Project object to a {@link ProjectDto.Details}.
   *
   * @param project           The Project object to be mapped.
   * @param timeRegistrations The user's time registrations.
   * @param userId The user's id to filter on user-specific details.
   *
   * @return A ProjectDTO.Details object
   */
  public static ProjectDto.Details toDetailsDto(Project project,
                                                List<TimeRegistration> timeRegistrations,
                                                Long userId) {
    var dto = new ProjectDto.Details();
    setIndexInformation(project, dto, userId);
    dto.description = project.description;
    dto.customer = new CustomerDto.Index(project.customer);
    dto.startDate = project.startDate;
    dto.tasks = project.tasks.stream().map(TaskDto.Index::new).toList();
    dto.timeRegistration = timeRegistrations.stream().map(TimeRegistrationMapper::toDto).toList();
    return dto;
  }


  /**
   * Maps a Project object to a ProjectDto.Name object.
   *
   * @param project The Project object to be mapped.
   * @return A Project.Name object mapped from the given Project.
   */
  public static ProjectDto.Name toNameDto(Project project) {
    if (project == null) {
      return null;
    }
    return new ProjectDto.Name(
        project.id,
        project.name
    );
  }

  public static boolean fromDto(ProjectDto.UpdateFavorite dto) {
    return dto.isFavorite();
  }

  private static void setIndexInformation(Project project, ProjectDto.Index dto, Long userId) {
    dto.id = project.id;
    dto.name = project.name;
    if (project.endDate == null) {
      dto.workMinutesLeft =
          project.timeRegistrations.stream()
              .filter(tr -> tr.isActive)
              .map(TimeRegistration::getWorkedMinutes)
              .reduce(project.totalWorkMinutes, (workMinutesLeft, next) -> workMinutesLeft - next);
    } else {
      dto.endDate = project.endDate;
    }
    var userProjectInfo =
        project.employees.stream().filter(it -> it.user.id.equals(userId)).findFirst();
    dto.isFavorite = userProjectInfo.orElseThrow().isFavorite;
    dto.whenFavorited = userProjectInfo.orElseThrow().updatedAt;
  }
}
