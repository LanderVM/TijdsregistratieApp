package world.inetumrealdolmen.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the customer of a {@link Project}.
 */
@Entity
@Table(name = "customer")
public class Customer extends BaseEntity {

  /**
   * The name of the customer.
   */
  @Column(name = "name", nullable = false)
  @NotBlank
  public String name = "";

  /**
   * The name of the company associated with the customer.
   */
  @Column(name = "company_name", nullable = false)
  @NotBlank
  public String companyName = "";

  /**
   * The projects this Customer is assigned to.
   */
  @OneToMany(
      mappedBy = "customer",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
  )
  @JsonManagedReference
  @NotNull
  public Set<Project> assignedProjects = new HashSet<>();
}
