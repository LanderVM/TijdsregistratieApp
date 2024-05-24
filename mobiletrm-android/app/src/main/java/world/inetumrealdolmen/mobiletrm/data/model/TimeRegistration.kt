package world.inetumrealdolmen.mobiletrm.data.model

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.intl.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import world.inetumrealdolmen.mobiletrm.data.dto.ProjectDTO
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationResult
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.ExistingTimeEntry
import world.inetumrealdolmen.mobiletrm.ui.validation.validations.ValidTimeEntry
import java.util.UUID

/**
 * Represents the state used in [world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration].
 * Used to create new [TimeRegistration]s or update an existing one.
 *
 * @param existingTimeRegistrationLocalId the local ID of the existing time registration, or null if it's a new time registration.
 * @param existingTimeRegistrationRemoteId the remote ID of the existing time registration, or null if it's a new time registration.
 * @param timeEntries the list of [TimeEntry] for time registrations.
 * @param projectTasks the list of [Task] to select from per [Project].
 * @param description the description of the [TimeRegistration]s to add.
 * @param project the ID of the [Project] the [TimeRegistration]s will belong to. Can not be null.
 * @param task the ID of the [Task] of the [TimeRegistration]s. Can be null.
 * @param tags the [Tag]s of the [TimeRegistration]s. Up to 10.
 * @param date the work day of the [TimeRegistration]s.
 * @param validationResults a list of [ValidationResult]. Can be called using [world.inetumrealdolmen.mobiletrm.ui.validation.Validator]
 * @param hasChanged Whether the user has changed the state or not. Used to display a confirmation message to leave the screen.
 */
data class PutTimeRegistrationState
    @OptIn(ExperimentalMaterial3Api::class)
    constructor(
        val existingTimeRegistrationLocalId: UUID?,
        val existingTimeRegistrationRemoteId: Long = -1L,
        @ExistingTimeEntry @ValidTimeEntry
        val timeEntries: PersistentList<TimeEntry> =
            persistentListOf(
                TimeEntry(id = -10),
                TimeEntry(id = -9),
            ),
        val projectTasks: ImmutableList<ProjectTasks> = persistentListOf(),
        val description: String = "",
        val project: ProjectDTO.IndexShort? = null,
        val task: Task? = null,
        val tags: PersistentSet<Tag> = persistentSetOf(),
        val date: DatePickerState = DatePickerState(Locale.current.platformLocale),
        val existingTimeEntries: ImmutableList<TimeEntry> = persistentListOf(),
        val validationResults: List<ValidationResult> = persistentListOf(),
        val hasChanged: Boolean = false,
    )

/**
 * Represents the state used in [world.inetumrealdolmen.mobiletrm.ui.screen.registrationtimer]
 * Used to create new [TimeRegistration]s using the timer mode.
 *
 * @param timeEntries the list of time entries to register.
 * @param canPause whether the timer can be paused or not.
 * @param canDiscard whether the timer has been started before or not.
 * @param canSubmit whether the timer can be submitted or not.
 * @param pauses the number of times the timer has been paused.
 * @param elapsedTime the elapsed time in milliseconds.
 */
data class RegistrationTimerState(
    val timeEntries: PersistentList<TimeEntry> = persistentListOf(),
    val canPause: Boolean = true,
    val canDiscard: Boolean = false,
    val canSubmit: Boolean = false,
    val pauses: Int = 0,
    val elapsedTime: Int = 0,
)

/**
 * Represents the state used in [world.inetumrealdolmen.mobiletrm.ui.screen.timeregistrationsoverview].
 *
 * @param timeRegistrations The list of time registrations to display.
 * @param runningProjectIds The list of running project IDs.
 */
data class TimeRegistrationIndexState(
    val timeRegistrations: ImmutableList<TimeRegistration> = persistentListOf(),
    val runningProjectIds: ImmutableList<Long> = persistentListOf(),
)

/**
 * An individual TimeEntry, used in [PutTimeRegistrationState] for a [TimeRegistration].
 */
data class TimeEntry(
    val id: Long = 0,
    val startTime: LocalTime = LocalTime(0, 0),
    var endTime: LocalTime = LocalTime(0, 0),
)

/**
 * The model of a TimeRegistration's index.
 *
 * @param remoteId The database ID of the time registration. -1 if it hasn't been persisted remotely yet.
 * @param localId The local database UUID of the time registration. UUID is generated if not provided.
 * @param description The optional description of the time registration.
 * @param startTime The start time of the time registration.
 * @param endTime The end time of the time registration.
 * @param assignedProject The optional project the time registration is for.
 * @param assignedTask The optional task the time registration is for.
 * @param tags The list of optional tags for the time registration.
 * @param isConcept Whether the time registration is a concept or not.
 * @param syncingError The error that occurred while syncing the time registration, or null if it hasn't been synced or there is no error.
 */
data class TimeRegistration(
    val remoteId: Long = -1,
    val localId: UUID = UUID.randomUUID(),
    val description: String?,
    val startTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val endTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val assignedTask: Task? = null,
    val assignedProject: ProjectDTO.IndexShort?,
    val tags: List<Tag> = emptyList(),
    val isConcept: Boolean = false,
    val syncingError: ErrorType? = null,
)
