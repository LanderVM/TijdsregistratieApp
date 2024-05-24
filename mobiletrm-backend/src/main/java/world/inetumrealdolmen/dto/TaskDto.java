package world.inetumrealdolmen.dto;

import world.inetumrealdolmen.domain.Task;

/**
 * A collection of Data Transfer Objects (DTO) for {@link Task} entities to be sent to clients.
 * Use the static subclasses' constructors to create DTO's.
 */
public class TaskDto {
  /**
   * Represents an index view of a task.
   */
  public static class Index {

    /**
     * The ID of the Task.
     */
    public long id;
    /**
     * The name of the task.
     */
    public String name;

    /**
     * Default constructor for Jackson serializing.
     * Protected to prevent instantiation without parameters.
     */
    public Index() {
    }

    /**
     * Constructor to convert a Task object into a DTO with basic information.
     *
     * @param task The Task object to convert.
     */
    public Index(Task task) {
      id = task.id;
      name = task.name;
    }
  }
}
