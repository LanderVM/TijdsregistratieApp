package world.inetumrealdolmen.mobiletrm.ui.validation

import androidx.annotation.StringRes
import world.inetumrealdolmen.mobiletrm.R

/**
 * State for a validation error.
 *
 * @param property the name of the property this validation error belongs to.
 * @param type the type of validation the [property] has failed.
 */
data class ValidationResult(
    val property: String = "",
    val type: ValidationErrorType = ValidationErrorType.NONE,
)

/**
 * Enum to define the type of validation error in forms.
 */
enum class ValidationErrorType(
    @StringRes val message: Int,
) {
    EMPTY(
        message = R.string.validations_notEmpty,
    ),
    NO_TIME_ENTRIES(
        message = R.string.validations_noTimeEntries,
    ),
    START_TIME_AFTER_END_TIME(
        message = R.string.validations_startTimeAfterEndTime,
    ),
    START_TIME_IS_END_TIME(
        message = R.string.validations_startTimeIsEndTime,
    ),
    OVERLAPPING_TIME_ENTRIES(
        message = R.string.validations_overlappingTimeEntries,
    ),
    OVERLAPPING_TIME_ENTRIES_IN_DB(
        message = R.string.validations_overlappingTimeEntriesInDb,
    ),
    NONE(
        message = R.string.validations_none,
    ),
}
