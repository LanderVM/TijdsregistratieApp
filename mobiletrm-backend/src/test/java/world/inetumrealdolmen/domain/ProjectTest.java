package world.inetumrealdolmen.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
public class ProjectTest {

  @Inject
  Validator validator;

  private static final String VALID_NAME = "MobileTRM Project";
  private static final String VALID_DESCRIPTION = "Microsoft";
  private static final LocalDate VALID_START_DATE = LocalDate.now();
  private static final LocalDate VALID_END_DATE = LocalDate.now().plusDays(30);
  private static final long VALID_TOTAL_WORK_MINUTES = 6000;
  private static Customer validCustomer;
  private static Project project;

  private static Stream<Arguments> getInvalidStartDates() {
    return Stream.of(
        Arguments.of((Object) null),
        Arguments.of(VALID_END_DATE.plusDays(1))
    );
  }

  private static Stream<Arguments> getInvalidTotalWorkMinutes() {
    return Stream.of(
        Arguments.of(-1L),
        Arguments.of(-10L),
        Arguments.of(Long.MIN_VALUE)
    );
  }

  @BeforeEach
  public void beforeEach() {
    validCustomer = new Customer();
    validCustomer.name = "John Doe";
    validCustomer.companyName = "Inetum-Realdolmen";

    project = new Project();
    project.name = VALID_NAME;
    project.description = VALID_DESCRIPTION;
    project.customer = validCustomer;
    project.startDate = VALID_START_DATE;
    project.updateEndDate(VALID_END_DATE);
  }

  @Test
  public void createProject_fixedDeadline_happyFlow() {
    Set<ConstraintViolation<Project>> violations = validator.validate(project);

    assertEquals(0, violations.size());
    assertEquals(VALID_NAME, project.name);
    assertEquals(VALID_DESCRIPTION, project.description);
    assertEquals(VALID_START_DATE, project.startDate);
    assertEquals(VALID_END_DATE, project.endDate);
    assertNull(project.totalWorkMinutes);
    assertEquals(validCustomer, project.customer);
    assertNotNull(project.tasks);
    assertEquals(0, project.tasks.size());
  }

  @Test
  public void createProject_variableDeadline_happyFlow() {
    project.updateTotalWorkMinutes(VALID_TOTAL_WORK_MINUTES);

    Set<ConstraintViolation<Project>> violations = validator.validate(project);

    assertEquals(0, violations.size());
    assertEquals(VALID_NAME, project.name);
    assertEquals(VALID_DESCRIPTION, project.description);
    assertEquals(VALID_START_DATE, project.startDate);
    assertNull(project.endDate);
    assertEquals(VALID_TOTAL_WORK_MINUTES, project.totalWorkMinutes);
    assertNull(project.endDate);
    assertEquals(validCustomer, project.customer);
    assertNotNull(project.tasks);
    assertEquals(0, project.tasks.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t"})
  public void createProject_invalidName_throwsIllegalArgumentException(String invalidName) {
    project.name = invalidName;

    Set<ConstraintViolation<Project>> violations = validator.validate(project);

    assertEquals(1, violations.size());
    assertEquals("must not be blank",
        violations.iterator().next().getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t"})
  public void createProject_invalidDescription_hasViolation(
      String invalidDescription) {
    project.description = invalidDescription;

    Set<ConstraintViolation<Project>> violations = validator.validate(project);

    assertEquals(1, violations.size());
    assertEquals("must not be blank if not null",
        violations.iterator().next().getMessage());
  }

  @ParameterizedTest
  @MethodSource("getInvalidStartDates")
  public void createProject_invalidStartDate_throwsIllegalArgumentException(
      LocalDate invalidStartDate) {
    assertThrows(IllegalArgumentException.class,
        () -> project.startDate = invalidStartDate);
  }

  @Test
  public void createProject_invalidEndDate_throwsIllegalArgumentException() {
    project.updateTotalWorkMinutes(null);
    assertThrows(IllegalArgumentException.class,
        () -> project.updateEndDate(null));
  }

  @Test
  public void createProject_endDateBeforeStartDate_throwsIllegalArgumentException() {
    project.updateTotalWorkMinutes(null);
    assertThrows(IllegalArgumentException.class,
        () -> project.updateEndDate(VALID_START_DATE.minusDays(10)));
  }

  @Test
  public void createProject_invalidCustomer_throwsIllegalArgumentException() {
    project.customer.name = "";

    Set<ConstraintViolation<Project>> violations = validator.validate(project);

    assertEquals(1, violations.size());
    assertEquals("must not be blank",
        violations.iterator().next().getMessage());
  }

  @Test
  public void createProject_invalidCustomer_isNull_hasViolation() {
    project.customer = null;

    Set<ConstraintViolation<Project>> violations = validator.validate(project);

    assertEquals(1, violations.size());
    assertEquals("must not be null",
        violations.iterator().next().getMessage());
  }

  @ParameterizedTest
  @MethodSource("getInvalidTotalWorkMinutes")
  public void createProject_variableDeadline_invalidTotalWorkMinutes_throwsIllegalArgumentException(
      Long invalidTotalWorkMinutes) {
    project.updateTotalWorkMinutes(invalidTotalWorkMinutes);

    Set<ConstraintViolation<Project>> violations = validator.validate(project);

    assertEquals(1, violations.size());
    assertEquals("must be strictly positive if not null",
        violations.iterator().next().getMessage());
  }

  @Test
  public void addTask_happyFlow() {
    Task task = new Task(VALID_DESCRIPTION, project);

    project.addTask(task);

    assertTrue(project.tasks.contains(task));
  }

  @Test
  public void addTask_isNull_throwsInvalidArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> project.addTask(null));
  }

  @Test
  public void addTask_alreadyInProject_throwsInvalidArgumentException() {
    Task task = new Task(VALID_NAME, project);

    project.addTask(task);

    assertThrows(IllegalArgumentException.class, () -> project.addTask(task));
  }

  @Test
  public void removeTask_happyFlow() {
    Task task = new Task(VALID_NAME, project);

    project.addTask(task);
    project.removeTask(task);

    assertFalse(project.tasks.contains(task));
  }

  @Test
  public void removeTask_belongsToDifferentProject_throwsInvalidArgumentException() {
    Project differentProject = new Project();

    Task task = new Task(VALID_NAME, differentProject);

    assertThrows(IllegalArgumentException.class, () -> project.removeTask(task));
  }

  @Test
  public void removeTask_isNull_throwsInvalidArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> project.addTask(null));
  }

  @Test
  public void removeTask_notInProject_throwsInvalidArgumentException() {
    Task task = new Task(VALID_NAME, project);

    assertThrows(IllegalArgumentException.class, () -> project.removeTask(task));
  }
}