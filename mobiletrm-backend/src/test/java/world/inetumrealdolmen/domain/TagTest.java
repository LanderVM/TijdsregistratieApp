package world.inetumrealdolmen.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class TagTest {

  @Inject
  Validator validator;

  private static final String VALID_NAME = "Valid tag";
  private static Tag tag;

  @BeforeEach
  public void init() {
    tag = new Tag();
  }

  @Test
  public void createTag_happyFlow() {
    tag.name = VALID_NAME;

    assertEquals(VALID_NAME, tag.name);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t"})
  public void createTag_invalidName(String invalidName) {
    tag.name = invalidName;

    Set<ConstraintViolation<Tag>> violations = validator.validate(tag);

    assertEquals(1, violations.size());
    assertEquals("must not be blank",
        violations.iterator().next().getMessage());
  }
}
