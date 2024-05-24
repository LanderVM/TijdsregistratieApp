package world.inetumrealdolmen.domain;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a tag (such as "Back-end") used in {@link Task}'s.
 */
@Entity
@Cacheable
@Table(name = "tag")
public class Tag extends BaseEntity {

  /**
   * The name of the tag with {@link LengthConstraint} SHORT.
   */
  @Column(name = "name", unique = true, nullable = false, length = LengthConstraint.SHORT)
  @NotBlank
  public String name = "";
}
