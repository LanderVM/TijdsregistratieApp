package world.inetumrealdolmen.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import world.inetumrealdolmen.validation.impl.NullOrNotBlankValidator;

/**
 * Custom Hibernate Validator to validate for strings to be either null or not blank.
 */
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public @interface NullOrNotBlank {
  String message() default "{ineturealdolmen.validation.constraints.NullOrNotBlank.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}