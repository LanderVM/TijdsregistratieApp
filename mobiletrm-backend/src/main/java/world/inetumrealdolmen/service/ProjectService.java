package world.inetumrealdolmen.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import world.inetumrealdolmen.domain.Project;
import world.inetumrealdolmen.domain.UserProject;

/**
 * Service class for managing {@link Project}-related operations.
 */
@ApplicationScoped
public class ProjectService {

  /**
   * Retrieves the list of projects assigned to a user.
   *
   * @param userId The user's ID to fetch projects for.
   * @return The list of projects assigned to the user.
   */
  public List<Project> getProjects(Long userId) {
    return Project.list("#Project.findAll", userId);
  }

  /**
   * Retrieves project details by ID.
   *
   * @param id     The ID of the project.
   * @param userId The user's ID.
   * @return the Project corresponding to the provided ID.
   * @throws NotFoundException if the project could not be found or is not active.
   */
  public Project getDetailsById(Long id, Long userId) {
    Project result = Project.find("#Project.findById", id, userId).firstResult();
    if (result == null || !result.isActive) {
      throw new NotFoundException();
    }
    return result;
  }

  /**
   * Marks a project as favorited or unfavorited.
   *
   * @param id         The ID of the project.
   * @param userId     The user's ID.
   * @param isFavorite Whether the project should be favorited or unfavorited.
   */
  @Transactional
  public boolean setFavorite(Long id, Long userId, Boolean isFavorite) {
    UserProject userProject =
        UserProject.find("project.id = ?1 and user.id = ?2", id, userId)
            .firstResult();

    if (userProject == null || !userProject.project.isActive) {
      throw new NotFoundException("Project with id " + id + " could not be found!");
    }

    userProject.isFavorite = isFavorite;
    userProject.persist();

    // Unfavorite previous project
    if (isFavorite) {
      List<UserProject> oldFavorite =
          UserProject.find("user.id = ?1 and isFavorite = true and project.id != ?2", userId, id)
              .list();
      oldFavorite.forEach(project -> {
        project.isFavorite = false;
        project.persist();
      });
    }

    return userProject.isFavorite;
  }
}
