package world.inetumrealdolmen.mobiletrm.ui.validation.validations

import kotlinx.datetime.LocalTime
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationResult

/**
 * Specifies that the current property must not be overlapping with a different property.
 * See [validateValidTimeEntry] for more details.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class ExistingTimeEntry

/**
 * Validates if property has a list of valid [TimeEntry].
 * Ignores time entries where [TimeEntry.startTime] and [TimeEntry.endTime] are 00:00.
 * Use [world.inetumrealdolmen.mobiletrm.ui.validation.Validator] to validate this annotation.
 *
 * @return [ValidationResult] the result of the validation.
 */
@Suppress("UNCHECKED_CAST")
fun validateExistingTimeEntry(
    propertyName: String,
    existingValues: Any?,
    chosenValues: Any?,
): List<ValidationResult> {
    if (existingValues !is List<*> || chosenValues !is List<*>) {
        throw UnsupportedOperationException("Expected values to a be a List")
    }

    if (existingValues.isEmpty() || chosenValues.isEmpty()) {
        return emptyList()
    }
    if (existingValues.any { it !is TimeEntry } || chosenValues.any { it !is TimeEntry }) {
        throw UnsupportedOperationException("Expected values to be a List<TimeEntry>")
    }

    return validateNonNullable(
        propertyName,
        existingValues as List<TimeEntry>,
        chosenValues as List<TimeEntry>,
    )
}

private fun validateNonNullable(
    propertyName: String,
    existingValues: List<TimeEntry>,
    chosenValues: List<TimeEntry>,
): List<ValidationResult> {
    val validationResults = mutableListOf<ValidationResult>()
    val existingValuesFiltered =
        existingValues.filter { !(it.startTime == LocalTime(0, 0) && it.endTime == LocalTime(0, 0)) }
            .sortedBy { it.startTime }

    for (chosenEntry in chosenValues) {
        for (existingEntry in existingValuesFiltered) {
            if (chosenEntry.id != existingEntry.id &&
                chosenEntry.endTime > existingEntry.startTime &&
                chosenEntry.startTime < existingEntry.endTime
            ) {
                validationResults +=
                    listOf(
                        ValidationResult(
                            "$propertyName[${chosenEntry.id}]",
                            ValidationErrorType.OVERLAPPING_TIME_ENTRIES_IN_DB,
                        ),
                    )
            }
        }
    }

    return validationResults
}
