package world.inetumrealdolmen.dto;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import world.inetumrealdolmen.TestUtils;

@QuarkusTest
public class ProjectResourceTest {

  static final Long VALID_PROJECT_ID = 1L;
  static final TestUtils TEST_UTILS = new TestUtils();

  @ConfigProperty(name = "OIDC_TEST_USERNAME")
  String testUsername;

  @ConfigProperty(name = "OIDC_TEST_PASSWORD")
  String testPassword;

  @AfterAll
  public static void close() {
    TEST_UTILS.close();
  }

  @Test
  void getProjectsOverview_happyFlow() {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .get("api/projects");

    response.then()
        .statusCode(HttpStatus.SC_OK);
    var parsedResponse = response.getBody().as(new TypeRef<List<ProjectDto.Index>>() {
    });

    assertTrue(parsedResponse.size() >= 2);
  }

  @Test
  void setFavorite_happyFlow() {
    var body = new ProjectDto.UpdateFavorite(true);

    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .contentType("application/json")
        .body(body)
        .put("api/projects/" + VALID_PROJECT_ID + "/favorite");

    var resultFromCall = response.then()
        .statusCode(HttpStatus.SC_OK)
        .extract().as(Boolean.class);
    var resultingProjectInfo = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .get("api/projects/" + VALID_PROJECT_ID)
        .getBody().as(ProjectDto.Details.class);

    assertTrue(resultFromCall);
    assertTrue(resultingProjectInfo.isFavorite);
  }

  @Test
  void setFavorite_noBody_returnsBadRequest() {
    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .contentType("application/json")
        .put("api/projects/" + VALID_PROJECT_ID + "/favorite")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void setFavorite_invalidBody_returnsBadRequest() {
    var body = new ProjectDto.UpdateFavorite(null);

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .contentType("application/json")
        .body(body)
        .put("api/projects/1/favorite")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void setFavorite_nonExistentProjectId_returnsNotFound() {
    var body = new ProjectDto.UpdateFavorite(true);

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .contentType("application/json")
        .body(body)
        .put("api/projects/9999/favorite")
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "-4777", "0"})
  void setFavorite_invalidProjectId_returnsBadRequest(String invalidId) {
    var body = new ProjectDto.UpdateFavorite(true);

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .contentType("application/json")
        .body(body)
        .put("api/projects/" + invalidId + "/favorite")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }


  @Test
  void getProjectDetails_happyFlow() {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .get("api/projects/" + VALID_PROJECT_ID);
    response.then()
        .statusCode(HttpStatus.SC_OK);
    var parsedResponse = response.getBody().as(ProjectDto.Details.class);
    assertEquals(VALID_PROJECT_ID, parsedResponse.id);
  }

  @Test
  void getProjectDetails_unknownId_returnsNotFound() {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .get("api/projects/99999999999999");
    response.then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void getProjectDetails_userNotPartOfProject_returnsNotFound() {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .get("api/projects/8");
    response.then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "-4777", "0"})
  void getProjectDetails_invalidId_returnsBadRequest(String invalidId) {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .get("api/projects/" + invalidId);
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }
}