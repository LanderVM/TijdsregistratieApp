package world.inetumrealdolmen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Embeddable ID for {@link UserProject}.
 */
@Embeddable
public class UserProjectId implements Serializable {

  /**
   * The user's ID.
   */
  @Column(name = "user_id")
  public Long userId;

  /**
   * The project's ID.
   */
  @Column(name = "project_id")
  public Long projectId;

  protected UserProjectId() {
  }

  public UserProjectId(Long userId, Long projectId) {
    this.userId = userId;
    this.projectId = projectId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserProjectId that = (UserProjectId) o;
    return Objects.equals(userId, that.userId)
        && Objects.equals(projectId, that.projectId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, projectId);
  }
}
