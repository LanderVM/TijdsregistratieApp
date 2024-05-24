package world.inetumrealdolmen.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import world.inetumrealdolmen.validation.NullOrNotBlank;

/**
 * Implementation for {@link NullOrNotBlank}.
 */
public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

  public void initialize(NullOrNotBlank parameters) {
    // Nothing to do here
  }

  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null || !value.trim().isEmpty();
  }
}
