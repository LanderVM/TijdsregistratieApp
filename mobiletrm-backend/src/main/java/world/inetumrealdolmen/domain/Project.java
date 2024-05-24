package world.inetumrealdolmen.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import world.inetumrealdolmen.validation.NullOrNotBlank;
import world.inetumrealdolmen.validation.NullOrPositive;

/**
 * Represents a project entity commonly used to create time registrations for.
 */
@Entity
@Table(name = "project")
@NamedQueries({
    @NamedQuery(
        name = "Project.findAll",
        query = """
            FROM Project p
            JOIN p.employees e
            WHERE e.id.userId = ?1 AND p.isActive = true
            """
    ),
    @NamedQuery(
        name = "Project.findById",
        query = """
            FROM Project p
            JOIN p.employees e
            WHERE p.id = ?1 AND e.id.userId = ?2 AND p.isActive = true
            """
    )
})
public class Project extends BaseEntity {

  /**
   * The name of the project.
   */
  @Column(name = "name", nullable = false)
  @NotBlank
  public String name = "";
  /**
   * The description of the project.
   */
  @Column(name = "description", length = LengthConstraint.MEDIUM)
  @NullOrNotBlank
  public String description = "";
  /**
   * The customer associated with the project.
   */
  @ManyToOne(
      cascade = CascadeType.ALL,
      optional = false,
      fetch = FetchType.LAZY
  )
  @JoinColumn(name = "customer_id", referencedColumnName = "id")
  @NotNull
  @Valid
  public Customer customer;
  /**
   * The start date of the project.
   */
  @Column(name = "start_date", nullable = false)
  public LocalDate startDate;
  /**
   * The fixed deadline of the project.
   * Will be null if the project has a total amount of work minutes instead.
   */
  @Column(name = "end_date")
  public LocalDate endDate;
  /**
   * The total amount of work minutes ordered for the project.
   * Will be null if the project has a fixed deadline instead.
   * Total amount of work minutes left can be calculated using the project's {@link #tasks}.
   */
  @Column(name = "total_work_minutes")
  @NullOrPositive
  public Long totalWorkMinutes;
  /**
   * The set of tasks associated with the project.
   */
  @OneToMany(
      mappedBy = "assignedProject",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
  )
  @JsonManagedReference
  public Set<@Valid Task> tasks = new HashSet<>();

  /**
   * The set of time entries that have been registered by employees working on the project.
   */
  @OneToMany(
      mappedBy = "assignedProject",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @JsonBackReference
  public Set<@Valid TimeRegistration> timeRegistrations = new HashSet<>();

  /**
   * The set of employees that have been assigned to the project.
   */
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
  @JsonBackReference
  public Set<UserProject> employees = new HashSet<>();

  /**
   * Sets the start date of the project.
   *
   * @param startDate The start date to set for the project.
   * @throws IllegalArgumentException if the provided start date is null or after the end date.
   */
  public void setStartDate(LocalDate startDate) {
    if (startDate == null) {
      throw new IllegalArgumentException("startDate cannot be null!");
    }
    if (endDate != null && endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("startDate can't be after endDate!");
    }
    this.startDate = startDate;
  }

  // Hibernate Panache setter => use updateTotalWorkMinutes() instead
  // Had to create a separate function to prevent StackOverflowError due to Panache's auto gen.
  private void setTotalWorkMinutes(Long totalWorkMinutes) {
    this.totalWorkMinutes = totalWorkMinutes;
  }

  // Hibernate Panache setter => use updateEndDate() instead
  // Had to create a separate function to prevent StackOverflowError due to Panache's auto gen.
  private void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  /**
   * Updates the Project with a new fixed deadline. Sets {@link #totalWorkMinutes} to null.
   *
   * @param endDate the new fixed deadline
   * @throws IllegalArgumentException if both {@link #endDate}
   *                                  and {@link #totalWorkMinutes} are null.
   * @throws IllegalArgumentException if {@link #endDate} is before {@link #startDate}.
   */
  public void updateEndDate(LocalDate endDate) {
    if (endDate == null && totalWorkMinutes == null) {
      throw new IllegalArgumentException("either endDate or totalWorkMinutes must be set!");
    }
    if (endDate != null) {
      if (endDate.isBefore(startDate)) {
        throw new IllegalArgumentException("endDate cannot be before startDate!");
      }
      this.totalWorkMinutes = null;
    }
    this.endDate = endDate;
  }

  /**
   * Updates the Project with a new variable deadline. Sets {@link #endDate} to null.
   *
   * @param totalWorkMinutes the new variable deadline
   * @throws IllegalArgumentException if both {@link #endDate}
   *                                  and {@link #totalWorkMinutes} are null.
   */
  public void updateTotalWorkMinutes(Long totalWorkMinutes) {
    if (endDate == null && totalWorkMinutes == null) {
      throw new IllegalArgumentException("either endDate or totalWorkMinutes must be set!");
    }
    if (totalWorkMinutes != null) {
      this.endDate = null;
    }
    this.totalWorkMinutes = totalWorkMinutes;
  }

  /**
   * Adds a task to the project.
   *
   * @param task The task to add.
   * @throws IllegalArgumentException if the provided task is null,
   *                                  already in the project or doesn't belong to the project.
   */
  public void addTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Task cannot be null!");
    }
    if (tasks.contains(task)) {
      throw new IllegalArgumentException("This project already contains this task!");
    }
    if (task.assignedProject != this) {
      throw new IllegalArgumentException("This task does not belong to this project!");
    }
    tasks.add(task);
  }

  /**
   * Removes a task from the project. Will remove the assigned project from the Task.
   *
   * @param task The task to remove.
   * @throws IllegalArgumentException if the provided task is null,
   *                                  no longer in the project or doesn't belong to the project.
   */
  public void removeTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Task cannot be null!");
    }
    if (task.assignedProject != this) {
      throw new IllegalArgumentException("This task does not belong to this project!");
    }
    if (!tasks.contains(task)) {
      throw new IllegalArgumentException("This project does not contain this task!");
    }

    tasks.remove(task);
  }
}
