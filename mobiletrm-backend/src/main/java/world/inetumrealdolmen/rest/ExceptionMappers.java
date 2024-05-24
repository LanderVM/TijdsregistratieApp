package world.inetumrealdolmen.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Mapper class used to map server exceptions to serve them to the client.
 */
@ApplicationScoped
public class ExceptionMappers {

  @Inject
  Logger log;

  /**
   * Maps an exception to a corresponding REST response.
   *
   * @param exception The exception to be mapped.
   * @return A {@code RestResponse<String>} object representing the mapped exception.
   */
  @ServerExceptionMapper
  public RestResponse<String> mapException(Exception exception) {
    var mapped = switch (exception) {
      case NoResultException e -> RestResponse.status(Response.Status.NOT_FOUND, e.getMessage());
      case NumberFormatException e ->
          RestResponse.status(Response.Status.BAD_REQUEST, e.getMessage());
      case IllegalArgumentException e ->
          RestResponse.status(Response.Status.BAD_REQUEST, e.getMessage());
      case BadRequestException e ->
          RestResponse.status(Response.Status.BAD_REQUEST, e.getMessage());
      case NotAllowedException e ->
          RestResponse.status(Response.Status.METHOD_NOT_ALLOWED, e.getMessage());
      case NotFoundException e -> RestResponse.status(Response.Status.NOT_FOUND, e.getMessage());
      default -> {
        Arrays.stream(exception.fillInStackTrace().getStackTrace()).limit(10).forEach(log::error);
        yield RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, exception.toString());
      }
    };
    log.infof("[%s] HTTP Exception %d mapped : %s",
        LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ISO_TIME),
        mapped.getStatus(),
        mapped.getEntity());
    return mapped;
  }
}
