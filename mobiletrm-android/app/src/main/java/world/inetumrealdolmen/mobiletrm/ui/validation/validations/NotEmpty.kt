package world.inetumrealdolmen.mobiletrm.ui.validation.validations

import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType.EMPTY
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType.NONE
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationResult
import java.lang.UnsupportedOperationException

/**
 * Specifies that the current property may not be empty.
 * See [validateNotEmpty] for more details.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class NotEmpty

/**
 * Validates if property is empty using [world.inetumrealdolmen.mobiletrm.ui.validation.Validator].
 * Currently supports: [Number] and [String]
 *
 * @return [ValidationResult] with [ValidationErrorType.EMPTY] if there is an error,
 *      OR [ValidationErrorType.NONE] if there is none.
 */
fun validateNotEmpty(
    propertyName: String,
    value: Any?,
): ValidationResult =
    when (value) {
        null -> ValidationResult(propertyName, EMPTY)
        else -> validateNonNullable(propertyName, value)
    }

private fun validateNonNullable(
    propertyName: String,
    value: Any,
): ValidationResult {
    val hasError: Boolean =
        when (value) {
            is Number -> value == 0
            is String -> value.isBlank()
            else -> throw UnsupportedOperationException()
        }
    return ValidationResult(
        propertyName,
        if (hasError) EMPTY else NONE,
    )
}
