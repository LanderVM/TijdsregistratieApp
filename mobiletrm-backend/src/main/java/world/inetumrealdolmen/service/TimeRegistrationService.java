package world.inetumrealdolmen.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import world.inetumrealdolmen.domain.BaseEntity;
import world.inetumrealdolmen.domain.Project;
import world.inetumrealdolmen.domain.Tag;
import world.inetumrealdolmen.domain.Task;
import world.inetumrealdolmen.domain.TimeRegistration;
import world.inetumrealdolmen.domain.User;
import world.inetumrealdolmen.dto.TimeRegistrationDto;
import world.inetumrealdolmen.mapper.TimeRegistrationMapper;

/**
 * Service class for managing {@link TimeRegistration}-related operations.
 */
@ApplicationScoped
public class TimeRegistrationService {

  /**
   * Create new time registrations for a project.
   *
   * @param request The {@link TimeRegistrationDto.Create} object containing the time entries.
   * @return The list of newly registered {@link TimeRegistration}'s.
   */
  @Transactional
  public List<TimeRegistration> createTimeRegistration(List<TimeRegistrationDto.Create> request,
                                                       Long userId) {
    var result = request.stream().map(tr -> createTimeRegistration(tr, userId)).toList();
    TimeRegistration.persist(result);
    return result;
  }

  private TimeRegistration createTimeRegistration(TimeRegistrationDto.Create request, Long userId) {
    if (request.startTime.isAfter(request.endTime) || request.startTime.isEqual(request.endTime)) {
      throw new BadRequestException("Start time cannot be after end time!");
    }

    if (TimeRegistration.hasOverlap(userId, request.startTime, request.endTime)) {
      throw new BadRequestException("Time entries may not overlap with existing registrations!");
    }

    Set<Tag> tags = new HashSet<>();
    addTagsFromDb(request, tags);

    if (request.description != null) {
      request.description = request.description.trim();
    }

    request.startTime = request.startTime.truncatedTo(ChronoUnit.SECONDS);
    request.endTime = request.endTime.truncatedTo(ChronoUnit.SECONDS);

    Project project = Project
        .find("#Project.findById", request.assignedProjectId, userId).firstResult();
    if (project == null) {
      throw new NotFoundException(
          "Project with id " + request.assignedProjectId + " +could not be found!");
    }

    Task task = request.assignedTaskId != null
        ? Task.find("#Task.findById", request.assignedTaskId, request.assignedProjectId)
        .singleResult() : null;
    TimeRegistration timeRegistration =
        TimeRegistrationMapper.fromDto(request);

    timeRegistration.assignedProject = project;
    timeRegistration.assignedTask = task;
    timeRegistration.tags = tags;
    timeRegistration.registrar = User.findById(userId);
    return timeRegistration;
  }

  /**
   * Retrieves every {@link TimeRegistration} in the database.
   *
   * @param userId The user's ID to query for.
   * @return A list of {@link TimeRegistration}.
   */
  @Transactional
  public List<TimeRegistration> getTimeRegistrations(Long userId) {
    return TimeRegistration.list("#TimeRegistration.findAll", userId);
  }

  /**
   * Retrieves every {@link TimeRegistration} in the database for a specific day.
   *
   * @param userId The user's ID to query for.
   * @param date   The day to query for.
   * @return the list of Time Registration for that day.
   */
  public List<TimeRegistration> getTimeRegistrations(Long userId, LocalDate date) {
    return TimeRegistration
        .find("#TimeRegistration.findByDate", userId, date)
        .list();
  }

  /**
   * Retrieves time registration details by ID.
   *
   * @param id     The ID of the time registration.
   * @param userId The user's ID to query for.
   * @return the TimeRegistration corresponding to the provided ID.
   * @throws NotFoundException if the project could not be found or is not active.
   */
  public TimeRegistration getDetailsById(Long id, Long userId) {
    TimeRegistration result =
        TimeRegistration.find("#TimeRegistration.findById", id, userId).firstResult();
    if (result == null || !result.isActive) {
      throw new NotFoundException();
    }
    return result;
  }

  /**
   * Deletes a time registration based on its ID.
   *
   * @param id     the ID of the time registration to delete.
   * @param userId The requesting user's ID.
   */
  @Transactional
  public void deleteTimeRegistration(Long id, Long userId) {
    Optional<TimeRegistration> timeRegistration =
        TimeRegistration.find("#TimeRegistration.findById", id, userId).firstResultOptional();
    timeRegistration.ifPresentOrElse(BaseEntity::setInactive, () -> {
      throw new NotFoundException("TimeRegistration with id " + id + " could not be found!");
    });
  }

  /**
   * Updates a time registration based on its ID and the request's body.
   *
   * @param request the request's body to update the time registration with.
   * @param userId  The requesting user's ID.
   * @return the ID of the updated time registration.
   */
  @Transactional
  public Long updateTimeRegistration(TimeRegistrationDto.Update request, Long userId) {
    if (request.startTime.isAfter(request.endTime) || request.startTime.isEqual(request.endTime)) {
      throw new BadRequestException("Start time cannot be after end time!");
    }

    if (TimeRegistration.hasOverlap(userId, request.startTime, request.endTime, request.id)) {
      throw new BadRequestException("Time entries may not overlap with existing registrations!");
    }

    Optional<TimeRegistration> timeRegistration =
        TimeRegistration.find("#TimeRegistration.findById", request.id, userId)
            .firstResultOptional();

    timeRegistration.ifPresent(registration -> {
      registration.description = request.description;
      registration.startTime = request.startTime;
      registration.endTime = request.endTime;


      if (request.assignedProjectId != null) {
        Optional<Project> projectFromDb =
            Project.find("#Project.findById", request.assignedProjectId, userId)
                .singleResultOptional();
        projectFromDb.ifPresentOrElse(project -> registration.assignedProject = project, () -> {
          throw new NotFoundException("The project id does not belong to any project!");
        });
      } else {
        registration.assignedProject = null;
      }

      if (request.assignedTaskId != null) {
        Optional<Task> taskFromDb =
            Task.find("#Task.findById", request.assignedTaskId, request.assignedProjectId)
                .firstResultOptional();
        taskFromDb.ifPresentOrElse(task -> registration.assignedTask = task, () -> {
          throw new NotFoundException("The task id does not belong to any task!");
        });
      } else {
        registration.assignedTask = null;
      }

      Set<Tag> tags = new HashSet<>();
      addTagsFromDb(request, tags);
      registration.tags = tags;
      registration.persist();
    });

    return timeRegistration.orElseThrow(() -> new NotFoundException(
        "The time registration id does not belong to any time registration!")).id;
  }

  private void addTagsFromDb(TimeRegistrationDto.Create request, Set<Tag> tags) {
    if (request.tags != null) {
      if (request.tags.size() > 10) {
        throw new IllegalArgumentException("There may only be up to 10 tags!");
      }

      request.tags.forEach(dto -> {
        Optional<Tag> fromDb = Tag.find("name", dto.name).singleResultOptional();
        fromDb.ifPresentOrElse(tags::add, () -> {
          Tag toPersist = new Tag();
          toPersist.name = dto.name;
          toPersist.persist();
          tags.add(toPersist);
        });
      });
    }
  }
}
