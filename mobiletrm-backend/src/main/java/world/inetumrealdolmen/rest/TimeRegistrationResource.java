package world.inetumrealdolmen.rest;

import io.quarkus.oidc.UserInfo;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;
import world.inetumrealdolmen.domain.TimeRegistration;
import world.inetumrealdolmen.domain.User;
import world.inetumrealdolmen.dto.TimeEntryDto;
import world.inetumrealdolmen.dto.TimeRegistrationDto;
import world.inetumrealdolmen.mapper.TimeEntryMapper;
import world.inetumrealdolmen.mapper.TimeRegistrationMapper;
import world.inetumrealdolmen.service.TimeRegistrationService;

/**
 * Resource class containing endpoints for handling requests related
 * to {@link TimeRegistration} in the API.
 */
@Path("/api/time-registrations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("employee")
public class TimeRegistrationResource {

  @Inject
  TimeRegistrationService service;

  @Inject
  UserInfo userInfo;

  /**
   * Creates new time registrations based on the provided request data.
   *
   * @param request A list of TimeRegistrationDto.Create objects representing the
   *                time registrations to be created.
   * @return A list of TimeRegistrationDto.CreateResponse objects.
   */
  @POST
  public List<TimeRegistrationDto.CreateResponse> create(
      @NotNull List<TimeRegistrationDto.@Valid Create> request) {
    var userId = User.findOrCreateUser(userInfo);
    var result = service.createTimeRegistration(request, userId);
    return result.stream().map(TimeRegistrationMapper::toCreateResponse).toList();
  }

  /**
   * Retrieves a list of all time registrations.
   *
   * @return A list of TimeRegistrationDto.Index objects representing all the time registrations.
   */
  @GET
  public List<TimeRegistrationDto.Index> getListTimeRegistrations() {
    var userId = User.findOrCreateUser(userInfo);
    return TimeRegistrationMapper.toDto(service.getTimeRegistrations(userId));
  }

  /**
   * Retrieves a time registration specified by its ID.
   *
   * @return A TimeRegistrationDto.Index object representing the time registration's requested data.
   */
  @GET
  @Path("/{id}")
  public TimeRegistrationDto.Index getTimeRegistration(
      @PathParam("id") @NotNull @Positive Long id) {
    var userId = User.findOrCreateUser(userInfo);
    var result = service.getDetailsById(id, userId);
    return TimeRegistrationMapper.toDto(result);
  }

  /**
   * Deletes a time registration specified by its ID.
   *
   * @param id The ID of the time registration to be deleted.
   */
  @DELETE
  @Path("/{id}")
  public void deleteTimeRegistrationById(
      @PathParam("id") @NotNull @Positive Long id) {
    var userId = User.findOrCreateUser(userInfo);
    service.deleteTimeRegistration(id, userId);
  }

  /**
   * Updates a time registration based on the provided request data.
   *
   * @param request A TimeRegistrationDto.Update object representing
   *                the updated data of the time registration.
   * @return The ID of the updated time registration.
   */
  @PUT
  public Long updateTimeRegistrationById(
      @Valid @NotNull TimeRegistrationDto.Update request) {
    var userId = User.findOrCreateUser(userInfo);
    return service.updateTimeRegistration(request, userId);
  }

  /**
   * Get the list of the user's time entries on a specific day.
   *
   * @param date The day to fetch time entries for.
   * @return The list of time entries on that day.
   */
  @GET
  @Path("/time-entries/{date}")
  public List<TimeEntryDto> getExistingTimeEntries(@PathParam("date") String date) {
    var userId = User.findOrCreateUser(userInfo);
    var results = service.getTimeRegistrations(userId, LocalDate.parse(date));

    return results.stream().map(TimeEntryMapper::toDto).toList();
  }
}
