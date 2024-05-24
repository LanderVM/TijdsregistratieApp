package world.inetumrealdolmen.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * Represents an identity from the Auth0 identity provider.
 * Match a {@link  User} from database
 * by passing {@link io.quarkus.oidc.UserInfo} into User.findOrCreateUser().
 */
@Entity(name = "auth0_identity")
public class Auth0Identity extends PanacheEntityBase {

  @Id
  public String auth0id;

  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "user_id")
  public User user = new User();
}
