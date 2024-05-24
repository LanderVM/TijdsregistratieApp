package world.inetumrealdolmen.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;

public class TaskTest {

  private static final String VALID_NAME = "GET Task Test";
  private static final String VALID_DESCRIPTION = "Valid description describing the task";
  private static final LocalDateTime VALID_START_DATE_TIME = LocalDateTime.now().minusHours(2);
  private static final LocalDateTime VALID_END_DATE_TIME = LocalDateTime.now();
  private static Project validProject;

  @BeforeEach
  public void beforeEach() {
    Customer validCustomer = new Customer();
    validCustomer.name = VALID_NAME;
    validCustomer.companyName = VALID_NAME;

    validProject = new Project();
    validProject.name = VALID_NAME;
    validProject.description = VALID_DESCRIPTION;
    validProject.customer = validCustomer;
    validProject.startDate = LocalDate.now().minusDays(20);
    validProject.endDate = LocalDate.now().minusDays(1);
  }

  @Test
  public void createTask_happyFlow() {
    var timeBetween = 120;

    Task task = new Task(VALID_NAME, validProject);

    assertEquals(VALID_NAME, task.name);
    assertEquals(validProject, task.assignedProject);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t"})
  public void createTask_invalidName_throwsIllegalArgumentException(String invalidName) {
    assertThrows(IllegalArgumentException.class,
        () -> new Task(invalidName, validProject));
  }

  @Test
  public void createTask_invalidProject_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Task(VALID_NAME, null));
  }
}
