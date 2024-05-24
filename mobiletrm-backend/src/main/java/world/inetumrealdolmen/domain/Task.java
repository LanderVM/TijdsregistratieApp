package world.inetumrealdolmen.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a task belonging to a {@link Project}.
 */
@Entity
@Table(name = "task")
@NamedQueries({
    @NamedQuery(
        name = "Task.findById",
        query = """
            FROM Task
            WHERE id = ?1 AND assignedProject.id = ?2
            """
    )
})
public class Task extends BaseEntity {

  /**
   * The name of the task with default LengthConstraint 255.
   */
  @Column(name = "name", nullable = false)
  public String name = "";

  /**
   * The project to which the task is assigned, mapped by {@link Project}.
   */
  @ManyToOne
  @JsonBackReference
  public Project assignedProject = new Project();

  /**
   * Set of time entries that have been registered for this task.
   */
  @OneToMany(
      mappedBy = "assignedTask",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
  )
  @JsonBackReference
  public Set<@Valid TimeRegistration> timeRegistrations = new HashSet<>();

  /**
   * Default constructor for Hibernate. Protected to prevent instantiation without parameters.
   */
  protected Task() {
  }

  /**
   * Creates a new instance of Task with the given parameters.
   *
   * @param name            The name of the task.
   * @param assignedProject The project to which the task is assigned.
   */
  public Task(String name, Project assignedProject) {
    setName(name);
    setAssignedProject(assignedProject);
  }

  /**
   * Updates the project this task belongs to.
   *
   * @param assignedProject The new project to assign.
   * @throws IllegalArgumentException if the provided project is null.
   */
  public void setAssignedProject(Project assignedProject) {
    if (assignedProject == null) {
      throw new IllegalArgumentException("project cannot be null!");
    }
    this.assignedProject = assignedProject;
  }

  /**
   * Sets the name of the task.
   *
   * @param name The name to set for the task.
   * @throws IllegalArgumentException if the provided name is empty or blank.
   */
  public void setName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name can't be empty");
    }
    this.name = name;
  }
}
