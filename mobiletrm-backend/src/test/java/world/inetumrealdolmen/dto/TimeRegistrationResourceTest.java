package world.inetumrealdolmen.dto;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.wildfly.common.Assert.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import world.inetumrealdolmen.TestUtils;
import world.inetumrealdolmen.domain.TimeRegistration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTest
public class TimeRegistrationResourceTest {

  private static final int MIN_VALID_ID = 0;
  static final TestUtils TEST_UTILS = new TestUtils();
  static final TimeRegistrationDto.Create VALID_CREATE = new TimeRegistrationDto.Create();
  static final TimeRegistrationDto.Update VALID_UPDATE = new TimeRegistrationDto.Update();


  @ConfigProperty(name = "OIDC_TEST_USERNAME")
  String testUsername;

  @ConfigProperty(name = "OIDC_TEST_PASSWORD")
  String testPassword;

  @BeforeEach
  public void init() {
    TagDto.Index tag = new TagDto.Index();
    tag.name = "this is tag 2";

    VALID_CREATE.description = "this is description";
    VALID_CREATE.startTime = LocalDateTime.of(2020, 10, 10, 10, 10);
    VALID_CREATE.endTime = LocalDateTime.of(2023, 9, 10, 10, 11);
    VALID_CREATE.assignedProjectId = 2L;
    VALID_CREATE.assignedTaskId = 10L;
    VALID_CREATE.tags = new HashSet<>();
    VALID_CREATE.tags.add(tag);

    VALID_UPDATE.id = 1000L;
    VALID_UPDATE.startTime = LocalDateTime.of(2024, 1, 1, 10, 11);
    VALID_UPDATE.endTime = LocalDateTime.of(2024, 1, 1, 10, 30);
    VALID_UPDATE.assignedProjectId = 1L;
    VALID_UPDATE.assignedTaskId = 1L;
    VALID_UPDATE.tags = new HashSet<>();
    VALID_UPDATE.description = "New description";
  }

  @AfterAll
  public static void close() {
    TEST_UTILS.close();
  }


  private Stream<Arguments> validCreateDtos() {
    TagDto.Index tag = new TagDto.Index();
    tag.name = "this is tag";

    TimeRegistrationDto.Create timeRegisShort = new TimeRegistrationDto.Create();
    timeRegisShort.startTime = LocalDateTime.of(2024, 10, 10, 10, 10);
    timeRegisShort.endTime = LocalDateTime.of(2024, 10, 10, 10, 11);
    timeRegisShort.assignedProjectId = 1L;
    timeRegisShort.tags = new HashSet<>();
    timeRegisShort.tags.add(tag);

    TimeRegistrationDto.Create timeRegisShortNoTags = new TimeRegistrationDto.Create();
    timeRegisShortNoTags.startTime = LocalDateTime.of(2020, 10, 10, 10, 10);
    timeRegisShortNoTags.endTime = LocalDateTime.of(2023, 9, 10, 10, 11);
    timeRegisShortNoTags.assignedProjectId = 1L;
    timeRegisShortNoTags.tags = new HashSet<>();

    return Stream.of(Arguments.of(timeRegisShort, timeRegisShortNoTags, VALID_CREATE));
  }

  private static Stream<Arguments> invalidTimeEntries() {
    return Stream.of(
        // End time before start time
        Arguments.of(LocalDateTime.now(), LocalDateTime.now().minusHours(1)),
        // End time is null
        Arguments.of(LocalDateTime.now(), null),
        // Start time is null
        Arguments.of(null, LocalDateTime.now())
    );
  }

  @MethodSource("validCreateDtos")
  @ParameterizedTest
  void postTimeRegistration_happyFlow(TimeRegistrationDto.Create timeRegis) {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(timeRegis))
        .when()
        .post("api/time-registrations");

    var body = response.then()
        .statusCode(HttpStatus.SC_OK).extract()
        .as(new TypeRef<List<TimeRegistrationDto.CreateResponse>>() {
        }).getFirst();

    assertTrue(body.id() > MIN_VALID_ID);
    assertEquals(body.description(), timeRegis.description);
    assertEquals(body.startTime(), timeRegis.startTime);
    assertEquals(body.endTime(), timeRegis.endTime);
    assertEquals(body.assignedProjectId(), timeRegis.assignedProjectId);
    assertEquals(body.assignedTaskId(), timeRegis.assignedTaskId);
    assertEquals(body.tags().size(), timeRegis.tags.size());
  }

  @Test
  void postTimeRegistration_invalidId_returnsBadRequest() {
    var invalidRegistration = VALID_CREATE;
    invalidRegistration.assignedProjectId = -1L;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(invalidRegistration))
        .when()
        .post("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "    ", "\n"})
  void postTimeRegistration_invalidDescription_returnsBadRequest(String invalidDescription) {
    var invalidRegistration = VALID_CREATE;
    invalidRegistration.description = invalidDescription;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(invalidRegistration))
        .when()
        .post("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @ParameterizedTest
  @MethodSource("invalidTimeEntries")
  void postTimeRegistration_invalidTimes_returnsBadRequest(LocalDateTime startTime,
                                                           LocalDateTime endTime) {
    var invalidRegistration = VALID_CREATE;
    invalidRegistration.startTime = startTime;
    invalidRegistration.endTime = endTime;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(invalidRegistration))
        .when()
        .post("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void postTimeRegistration_userNotPartOfProject_returnsNotFound() {
    var invalidRegistration = VALID_CREATE;
    invalidRegistration.assignedProjectId = 7L;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(invalidRegistration))
        .when()
        .post("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void postTimeRegistration_taskNotPartOfProject_returnsNotFound() {
    var invalidRegistration = VALID_CREATE;
    invalidRegistration.assignedProjectId = 1L;
    invalidRegistration.assignedTaskId = 11L;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(invalidRegistration))
        .when()
        .post("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void updateTimeRegistration_happyFlow() {
    var createResponse = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(VALID_CREATE))
        .when()
        .post("api/time-registrations");

    var createBody = createResponse.then()
        .statusCode(HttpStatus.SC_OK).extract()
        .as(new TypeRef<List<TimeRegistrationDto.CreateResponse>>() {
        }).getFirst();

    Long updateResponse = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(VALID_UPDATE)
        .put("api/time-registrations")
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract().as(Long.class);

    System.out.println(updateResponse);

    var updateFetchDetails = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .get("api/time-registrations/" + updateResponse);

    var updateFetchDetailsBody = updateFetchDetails.then()
        .statusCode(HttpStatus.SC_OK).extract()
        .as(new TypeRef<TimeRegistrationDto.Index>() {
        });

    assertEquals(createBody.id(), updateFetchDetailsBody.id);
    assertNotEquals(createBody.description(), updateFetchDetailsBody.description);
    assertNotEquals(createBody.startTime(), updateFetchDetailsBody.startTime);
    assertNotEquals(createBody.endTime(), updateFetchDetailsBody.endTime);
    assertNotEquals(createBody.assignedProjectId(), updateFetchDetailsBody.assignedProject.id());
    assertNotEquals(createBody.assignedTaskId(), updateFetchDetailsBody.assignedTask.id);
    assertNotEquals(createBody.tags().size(), updateFetchDetailsBody.tags.size());
  }

  @Test
  void putTimeRegistration_noProjectId_returnsBadRequest() {
    var invalidRegistration = VALID_CREATE;
    invalidRegistration.assignedProjectId = null;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(invalidRegistration)
        .when()
        .post("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void putTimeRegistration_invalidId_returnsBadRequest() {
    var invalidRegistration = VALID_UPDATE;
    invalidRegistration.assignedProjectId = -1L;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(invalidRegistration)
        .put("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "    ", "\n"})
  void putTimeRegistration_invalidDescription_returnsBadRequest(String invalidDescription) {
    var invalidRegistration = VALID_UPDATE;
    invalidRegistration.description = invalidDescription;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(invalidRegistration)
        .when()
        .put("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @ParameterizedTest
  @MethodSource("invalidTimeEntries")
  void putTimeRegistration_invalidTimes_returnsBadRequest(LocalDateTime startTime,
                                                          LocalDateTime endTime) {
    var invalidRegistration = VALID_UPDATE;
    invalidRegistration.startTime = startTime;
    invalidRegistration.endTime = endTime;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(invalidRegistration)
        .put("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void putTimeRegistration_timesOverlapWithDatabaseTimeRegistration_returnsBadRequest() {
    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(VALID_CREATE))
        .when()
        .post("api/time-registrations");

    var invalidRegistration = VALID_CREATE;
    invalidRegistration.startTime = invalidRegistration.startTime.minusHours(1);
    invalidRegistration.endTime = invalidRegistration.endTime.minusHours(1);

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(List.of(invalidRegistration))
        .when()
        .post("api/time-registrations")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void putTimeRegistration_userNotPartOfProject_returnsNotFound() {
    var invalidRegistration = VALID_UPDATE;
    invalidRegistration.assignedProjectId = 7L;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(invalidRegistration)
        .put("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void putTimeRegistration_taskNotPartOfProject_returnsNotFound() {
    var invalidRegistration = VALID_UPDATE;
    invalidRegistration.assignedProjectId = 1L;
    invalidRegistration.assignedTaskId = 73L;

    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .contentType("application/json")
        .body(invalidRegistration)
        .put("api/time-registrations/")
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "-6", "0"})
  void deleteTimeRegistration_invalidId_returnsBadRequest(String invalidId) {
    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .delete("api/time-registrations/" + invalidId)
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void deleteTimeRegistrationById_happyFlow() {
    given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .delete("api/time-registrations/1")
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);

    TimeRegistration timeRegistration = TimeRegistration.findById(1);
    assertFalse(timeRegistration.isActive);
  }

  @Test
  void deleteTimeRegistrationById_notUsersRegistration_returnsNotFound() {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .delete("api/time-registrations/154");
    response.then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void getTimeRegistrationById_notUsersRegistration_returnsNotFound() {
    var response = given()
        .auth().oauth2(TEST_UTILS.getAccessToken(testUsername, testPassword))
        .when()
        .get("api/time-registrations/154");
    response.then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }
}