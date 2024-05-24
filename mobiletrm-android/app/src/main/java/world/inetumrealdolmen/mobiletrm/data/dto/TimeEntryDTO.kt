package world.inetumrealdolmen.mobiletrm.data.dto

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry

/**
 * A time entry of a time registration from the api.
 *
 * @param startTime The start time of the time entry.
 * @param endTime The end time of the time entry.
 */
@Serializable
data class TimeEntryDTO(
    val id: Long,
    val startTime: LocalTime,
    val endTime: LocalTime,
)

/**
 * Converts the response data to the [TimeEntry] model.
 */
fun TimeEntryDTO.asDomainObject(): TimeEntry =
    TimeEntry(
        id = id,
        startTime = startTime,
        endTime = endTime,
    )

/**
 * Converts the response data to a list of [TimeEntry] models.
 */
fun List<TimeEntryDTO>.asDomainObjects(): List<TimeEntry> = map { it.asDomainObject() }
