package world.inetumrealdolmen.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Intermediary table defining a {@link User}'s relationship with {@link Project}.
 */
@Entity
@Table(name = "account_project")
public class UserProject extends PanacheEntityBase {

  @EmbeddedId
  public UserProjectId id;

  /**
   * The user.
   */
  @ManyToOne
  @MapsId("userId")
  public User user;

  /**
   * The project.
   */
  @ManyToOne
  @MapsId("projectId")
  public Project project;

  /**
   * Whether the user has this project in his list of favorites or not.
   */
  @Column(name = "is_favorite")
  public boolean isFavorite = false;

  /**
   * The timestamp when this entity was created.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  /**
   * The timestamp when this entity was last updated.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  public LocalDateTime updatedAt;
}
