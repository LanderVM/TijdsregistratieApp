package world.inetumrealdolmen.dto;

import jakarta.validation.constraints.NotBlank;
import world.inetumrealdolmen.domain.Tag;

/**
 * A collection of Data Transfer Objects (DTO) for {@link Tag} entities to be sent to clients.
 * Use the static subclasses' constructors to create DTO's.
 */
public class TagDto {
  /**
   * Represents an index view of a tag.
   */
  public static class Index {
    /**
     * The name of the tag.
     */
    @NotBlank
    public String name;
  }
}
