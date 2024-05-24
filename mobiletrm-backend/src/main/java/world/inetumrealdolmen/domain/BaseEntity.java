package world.inetumrealdolmen.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * This class serves as a base entity for other entities in the application.
 * It contains common fields like isActive, createdAt, and updatedAt
 * that get updated on database insertion/modification.
 * This extends PanacheEntity, which provides a default id field as well.
 */
@MappedSuperclass
public class BaseEntity extends PanacheEntity {

  /**
   * Indicates whether this entity has been deleted.
   */
  @Column(name = "is_active", nullable = false)
  public boolean isActive = true;

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
  private LocalDateTime updatedAt;

  public void setInactive() {
    isActive = false;
  }
}