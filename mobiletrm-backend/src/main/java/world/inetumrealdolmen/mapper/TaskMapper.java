package world.inetumrealdolmen.mapper;

import world.inetumrealdolmen.domain.Task;
import world.inetumrealdolmen.dto.TaskDto;

/**
 * Provides static methods to map between {@link Task} objects and {@link TaskDto} objects.
 */
public class TaskMapper {

  /**
   * Maps a Task object to a TaskDto.Index object.
   *
   * @param task The Task object to be mapped.
   * @return A TaskDto.Index object mapped from the given Task object.
   */
  public static TaskDto.Index toDto(Task task) {
    if (task == null) {
      return null;
    }
    var dto = new TaskDto.Index();
    dto.id = task.id;
    dto.name = task.name;
    return dto;
  }
}
