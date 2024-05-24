@file:Suppress("UNCHECKED_CAST")

package world.inetumrealdolmen.mobiletrm.ui.validation.validations

import kotlinx.datetime.LocalTime
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType.NO_TIME_ENTRIES
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType.OVERLAPPING_TIME_ENTRIES
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType.START_TIME_AFTER_END_TIME
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationErrorType.START_TIME_IS_END_TIME
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationResult

/**
 * Specifies that the current property must be a valid time entry.
 * See [validateValidTimeEntry] for more details.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class ValidTimeEntry

/**
 * Validates if property has a list of valid [TimeEntry].
 * Ignores time entries where [TimeEntry.startTime] and [TimeEntry.endTime] are 00:00.
 * Use [world.inetumrealdolmen.mobiletrm.ui.validation.Validator] to validate this annotation.
 *
 * @return [ValidationResult] the result of the validation.
 */
fun validateValidTimeEntry(
    propertyName: String,
    values: Any?,
): List<ValidationResult> {
    if (values !is List<*>) {
        throw UnsupportedOperationException("Expected values to a be a List")
    }
    if (values.isEmpty()) {
        return emptyList()
    }
    if (values.any { it !is TimeEntry }) {
        throw UnsupportedOperationException("Expected values to be a List<TimeEntry>")
    }

    return validateNonNullable(propertyName, values as List<TimeEntry>)
}

private fun validateNonNullable(
    propertyName: String,
    values: List<TimeEntry>,
): List<ValidationResult> {
    val validationResults = mutableListOf<ValidationResult>()
    val timeEntries =
        values
            .filter { !(it.startTime == LocalTime(0, 0) && it.endTime == LocalTime(0, 0)) }
            .sortedBy { it.startTime }

    if (timeEntries.isEmpty()) {
        validationResults += ValidationResult("$propertyName[-10]", NO_TIME_ENTRIES)
        return validationResults
    }

    timeEntries.forEach {
        if (it.startTime > it.endTime) {
            validationResults +=
                ValidationResult(
                    "$propertyName[${it.id}]",
                    START_TIME_AFTER_END_TIME,
                )
        }
        if (it.startTime == it.endTime) {
            validationResults +=
                ValidationResult(
                    "$propertyName[${it.id}]",
                    START_TIME_IS_END_TIME,
                )
        }
    }

    for (i in timeEntries.indices) {
        for (j in i + 1 until timeEntries.size) {
            val entry1 = timeEntries[i]
            val entry2 = timeEntries[j]

            if (entry1.endTime > entry2.startTime && entry1.startTime < entry2.endTime) {
                validationResults +=
                    listOf(
                        ValidationResult(
                            "$propertyName[${entry1.id}]",
                            OVERLAPPING_TIME_ENTRIES,
                        ),
                        ValidationResult(
                            "$propertyName[${entry2.id}]",
                            OVERLAPPING_TIME_ENTRIES,
                        ),
                    )
            }
        }
    }

    return validationResults
}
