package world.inetumrealdolmen.domain;

/**
 * This interface defines length constraints commonly
 * used in entity column configurations for Hibernate.
 * It provides constants representing short and medium character lengths for strings.
 */
public interface LengthConstraint {

  /**
   * Used for short names with up to 40 characters such as the name in {@link Tag}.
   */
  int SHORT = 40;

  /**
   * Used for names with up to 767 characters (the limit for InnoDB)
   * such as the description in {@link Project}.
   */
  int MEDIUM = 767;
}
