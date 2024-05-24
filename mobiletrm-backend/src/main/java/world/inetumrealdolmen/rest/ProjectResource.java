package world.inetumrealdolmen.rest;

import io.quarkus.oidc.UserInfo;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import world.inetumrealdolmen.domain.Project;
import world.inetumrealdolmen.domain.User;
import world.inetumrealdolmen.dto.ProjectDto;
import world.inetumrealdolmen.mapper.ProjectMapper;
import world.inetumrealdolmen.service.ProjectService;
import world.inetumrealdolmen.service.TimeRegistrationService;

/**
 * Resource class containing endpoints for handling requests related to {@link Project} in the API.
 */
@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("employee")
public class ProjectResource {

  @Inject
  ProjectService projectService;

  @Inject
  TimeRegistrationService timeRegistrationService;

  @Inject
  UserInfo userInfo;

  /**
   * Retrieves the list of projects for the user.
   *
   * @return The list of available projects assigned to the user.
   */
  @GET
  public List<ProjectDto.Index> getListProjects() {
    var userId = User.findOrCreateUser(userInfo);
    var results = projectService.getProjects(userId);
    return results.stream().map(it -> ProjectMapper.toDto(it, userId)).toList();
  }

  /**
   * Retrieves project details by ID.
   *
   * @param id The ID of the project to query for.
   * @return Detailed information of the available project.
   */
  @GET
  @Path("/{id}")
  public ProjectDto.Details getProjectDetailsById(
      @PathParam("id") @Positive @NotNull Long id) {
    var userId = User.findOrCreateUser(userInfo);
    var result = projectService.getDetailsById(id, userId);
    var timeRegistrations = timeRegistrationService.getTimeRegistrations(userId);
    return ProjectMapper.toDetailsDto(result, timeRegistrations, userId);
  }

  /**
   * Retrieves project details by ID.
   *
   * @param id The ID of the project to query for.
   * @param isFavorite Whether the project should be favorited or unfavorited.
   * @return The new favorite state of the project.
   */
  @PUT
  @Path("/{id}/favorite")
  public boolean favoriteProjectById(
      @PathParam("id") @Positive @NotNull Long id,
      @NotNull @Valid ProjectDto.UpdateFavorite isFavorite) {
    var userId = User.findOrCreateUser(userInfo);
    return projectService.setFavorite(id, userId, ProjectMapper.fromDto(isFavorite));
  }

  /**
   * Retrieves a list of all tasks associated with a project.
   *
   * @return The list of project's with their tasks.
   */
  @GET
  @Path("/tasks")
  public List<ProjectDto.Tasks> getProjectTasks() {
    var userId = User.findOrCreateUser(userInfo);
    var results = projectService.getProjects(userId);
    return results.stream().map(ProjectDto.Tasks::new).toList();
  }
}

