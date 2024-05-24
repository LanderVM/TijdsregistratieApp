package world.inetumrealdolmen;

import io.quarkus.test.oidc.client.OidcTestClient;
import java.util.Map;
import java.util.Objects;

public class TestUtils {

  private final OidcTestClient oidcTestClient = new OidcTestClient();

  private String accessToken;
  private String currentUserName;
  private String currentSecret;

  public void close() {
    oidcTestClient.close();
  }

  public String getAccessToken(String name, String secret) {
    if (accessToken == null
        || !Objects.equals(name, currentUserName)
        || !Objects.equals(secret, currentSecret)) {
      accessToken = oidcTestClient.getAccessToken(name, secret,
          Map.of("audience", "https://inetum-realdolmen.world/mobiletrm-geo",
              "scope", "openid profile"));
      currentUserName = name;
      currentSecret = secret;
    }
    return accessToken;
  }
}
