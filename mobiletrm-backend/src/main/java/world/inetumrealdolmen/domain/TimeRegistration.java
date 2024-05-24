package world.inetumrealdolmen.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import world.inetumrealdolmen.validation.NullOrNotBlank;

/**
 * Represents a time registration belonging to a {@link Project}.
 */
@Entity
@Table(name = "time_registration")
@NamedQueries({
    @NamedQuery(
        name = "TimeRegistration.findAll",
        query = """
            FROM TimeRegistration
            WHERE registrar.id = ?1 AND isActive = true
            """
    ),
    @NamedQuery(
        name = "TimeRegistration.findById",
        query = """
            FROM TimeRegistration
            WHERE id = ?1 AND registrar.id = ?2 AND isActive = true
            """
    ),
    @NamedQuery(
        name = "TimeRegistration.findOverlappingEntries",
        query = """
            FROM TimeRegistration
            WHERE
                registrar.id = ?1 AND isActive = true
            AND
                (
                    (?2 < endTime AND ?3 > startTime)
                    OR (?2 >= startTime AND ?2 < endTime)
                    OR (?3 > startTime AND ?3 <= endTime)
                )
            """
    ),
    @NamedQuery(
        name = "TimeRegistration.findByDate",
        query = """
            FROM TimeRegistration
            WHERE
                isActive = true AND registrar.id = ?1
            AND
                CAST(startTime AS date) = ?2
            """
    )
})
public class TimeRegistration extends BaseEntity {

  /**
   * The description of the time registration with {@link LengthConstraint} MEDIUM.
   */
  @Column(name = "description", length = LengthConstraint.MEDIUM)
  @NullOrNotBlank
  public String description;

  /**
   * The moment the employee started working.
   */
  @Column(name = "start_time")
  public LocalDateTime startTime;

  /**
   * The moment the employee finished working.
   */
  @Column(name = "end_time")
  public LocalDateTime endTime;

  /**
   * The project to which the time registration is assigned.
   */
  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  public Project assignedProject;

  /**
   * The task to which the time registration is assigned, mapped by {@link Task}.
   */
  @ManyToOne
  @JsonManagedReference
  public Task assignedTask;


  /**
   * The set of tags associated with the time registration.
   */
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
      name = "time_registration_tag",
      joinColumns = @JoinColumn(name = "time_registration_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
  )
  @NotNull
  public Set<Tag> tags = new HashSet<>();

  /**
   * The user that registered this time entry.
   */
  @ManyToOne
  public User registrar = new User();

  /**
   * Checks if there is an already existing time registration with overlapping time entries.
   *
   * @param userId    The ID of the user to check their time registrations for.
   * @param startTime The startTime to check for overlap.
   * @param endTime   The endTime to check for overlap.
   * @return True if there is an overlap
   */
  public static boolean hasOverlap(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
    var result =
        find("#TimeRegistration.findOverlappingEntries", userId, startTime, endTime);
    return result.count() != 0;
  }

  /**
   * Checks if there is an already existing time registration with overlapping time entries.
   * This will ignore a specific time entry as defined in the ignoredEntry parameter.
   *
   * @param userId    The ID of the user to check their time registrations for.
   * @param startTime The startTime to check for overlap.
   * @param endTime   The endTime to check for overlap.
   * @return True if there is an overlap
   */
  public static boolean hasOverlap(Long userId, LocalDateTime startTime, LocalDateTime endTime,
                                   Long ignoredEntry) {
    List<TimeRegistration> result =
        find("#TimeRegistration.findOverlappingEntries",
            userId, startTime, endTime).list();

    return result.stream().anyMatch(it -> !it.id.equals(ignoredEntry));
  }

  /**
   * Retrieves the time worked in minutes.
   *
   * @return the worked minutes in long.
   */
  public long getWorkedMinutes() {
    return ChronoUnit.MINUTES.between(startTime, endTime);
  }
}
