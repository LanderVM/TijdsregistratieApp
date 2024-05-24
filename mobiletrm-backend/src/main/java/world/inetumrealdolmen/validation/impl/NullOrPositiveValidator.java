package world.inetumrealdolmen.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import world.inetumrealdolmen.validation.NullOrPositive;


/**
 * Implementation for {@link NullOrPositive}.
 */
public class NullOrPositiveValidator implements ConstraintValidator<NullOrPositive, Long> {

  public void initialize(NullOrPositive parameters) {
    // Nothing to do here
  }

  public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null || value > 0;
  }
}
