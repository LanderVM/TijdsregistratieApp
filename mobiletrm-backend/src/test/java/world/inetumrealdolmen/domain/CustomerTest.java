package world.inetumrealdolmen.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
public class CustomerTest {

  static final String VALID_NAME = "John Doe";
  static final String VALID_COMPANY_NAME = "InetumRealdolmen";
  static Customer customer;

  @Inject
  Validator validator;

  @BeforeEach
  public void init() {
    customer = new Customer();
    customer.name = VALID_NAME;
    customer.companyName = VALID_COMPANY_NAME;
  }

  @Test
  public void createCustomer_happyFlow() {
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

    assertEquals(0, violations.size());
    assertEquals(VALID_NAME, customer.name);
    assertEquals(VALID_COMPANY_NAME, customer.companyName);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t"})
  public void createCustomer_invalidName(String invalidName) {
    customer.name = invalidName;

    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

    assertEquals(1, violations.size());
    assertEquals("must not be blank",
        violations.iterator().next().getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t"})
  public void createCustomer_invalidCompanyName(String invalidCompanyName) {
    customer.companyName = invalidCompanyName;

    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

    assertEquals(1, violations.size());
    assertEquals("must not be blank",
        violations.iterator().next().getMessage());
  }

  @Test
  public void createCustomer_invalidProjects() {
    customer.assignedProjects = null;

    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

    assertEquals(1, violations.size());
    assertEquals("must not be null",
        violations.iterator().next().getMessage());
  }
}
