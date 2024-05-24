package world.inetumrealdolmen.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import world.inetumrealdolmen.validation.impl.NullOrPositiveValidator;


/**
 * Custom Hibernate Validator to validate for numbers to be either strictly positive or null.
 */
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NullOrPositiveValidator.class)
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public @interface NullOrPositive {
  String message() default "{ineturealdolmen.validation.constraints.NullOrPositive.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}