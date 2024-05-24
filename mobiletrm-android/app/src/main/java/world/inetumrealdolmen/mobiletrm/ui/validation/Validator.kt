package world.inetumrealdolmen.mobiletrm.ui.validation

import kotlinx.collections.immutable.toImmutableList
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType.NONE
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.ExistingTimeEntry
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.NotEmpty
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.ValidTimeEntry
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.validateExistingTimeEntry
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.validateNotEmpty
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.validateValidTimeEntry
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

/**
 * Validator class to validate properties based on annotations. See [validate].
 *
 */
class Validator<State : Any>(
    private val kClass: KClass<State>,
) {
    /**
     * Validation function to validate properties based on the following annotations:
     * [NotEmpty]
     *
     * @return a list of [ValidationResult], or an empty list if there are none.
     */
    fun validate(
        state: State,
        chosenTimeEntries: List<TimeEntry>,
    ): List<ValidationResult> {
        val validationResults: MutableList<ValidationResult> = mutableListOf()

        kClass.memberProperties.forEach { property ->
            if (property.annotations.isEmpty()) {
                return@forEach
            }

            val propertyName = property.name
            val value = property.get(state)

            property.annotations.forEach { annotation ->
                val errors: List<ValidationResult> =
                    when (annotation) {
                        is NotEmpty -> listOf(validateNotEmpty(propertyName, value))
                        is ValidTimeEntry -> validateValidTimeEntry(propertyName, value)
                        is ExistingTimeEntry ->
                            validateExistingTimeEntry(
                                propertyName,
                                chosenTimeEntries,
                                value,
                            )

                        else -> listOf(ValidationResult(propertyName, NONE))
                    }
                validationResults += errors.filter { it.type != NONE }
            }
        }

        return validationResults.toImmutableList()
    }
}
