package world.inetumrealdolmen.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.oidc.UserInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user, identified by {@link Auth0Identity}.
 */
@Entity(name = "account")
public class User extends BaseEntity {

  /**
   * The identity of the user from the external identity provider.
   */
  @OneToOne(mappedBy = "user")
  public Auth0Identity identity;

  /**
   * The user's first name.
   */
  @Column(name = "first_name")
  public String firstName;

  /**
   * The user's family name.
   */
  @Column(name = "family_name")
  public String familyName;

  /**
   * The list of projects this user is assigned to.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<UserProject> projects = new ArrayList<>();

  /**
   * The list of time entries the user has registered.
   */
  @OneToMany(mappedBy = "registrar")
  public List<TimeRegistration> timeRegistrations = new ArrayList<>();

  /**
   * Finds a User based on their auth0id or creates a new User if the auth0id is new.
   *
   * @param request the UserInfo object from the API request to get the subject from.
   * @return the User's id
   */
  @Transactional
  public static Long findOrCreateUser(UserInfo request) {
    Auth0Identity result = Auth0Identity.find("auth0id", request.getSubject()).firstResult();
    if (result == null) {
      var newUser = new Auth0Identity();
      newUser.auth0id = request.getSubject();
      newUser.user.firstName = request.getString("given_name");
      newUser.user.familyName = request.getFamilyName();
      newUser.persist();
      return newUser.user.id;
    }
    return result.user.id;
  }

}
