package world.inetumrealdolmen.mobiletrm.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import world.inetumrealdolmen.mobiletrm.ui.util.now

/**
 * Represents the state used in [world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails].
 *
 * @param details The details of a project, or null if there aren't any.
 */
data class ProjectDetailsState(
    val details: ProjectDetails,
)

/**
 * The model of a Project's index.
 * A project has either an [endDate] or an amount of [workMinutesLeft].
 *
 * @param id The database ID of the project.
 * @param name The name of the project.
 * @param endDate The end date of the project (nullable).
 * @param workMinutesLeft The remaining work time in minutes (nullable).
 * @param isFavorite Whether the project is starred (default is false).
 * @param whenFavorited When the project was last (un)favorited.
 */
data class Project(
    val id: Long = -1L,
    val name: String = "",
    val endDate: LocalDate? = null,
    val workMinutesLeft: Long? = null,
    var isFavorite: Boolean = false,
    val whenFavorited: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
)

/**
 * The model of a Project with more detailed information.
 * A project has either an [endDate] or an amount of [workMinutesLeft].
 *
 * @param id The database ID of the project.
 * @param name The name of the project.
 * @param description The description of the project.
 * @param customer the Customer associated the project.
 * @param startDate the starting date of the project.
 * @param endDate The end date of the project. Null if working with work minutes remaining.
 * @param workMinutesLeft The remaining work time in minutes. Null if working with a fixed deadline.
 * @param tasks The list of [Task] associated with the project.
 * @param isStarred Whether the project is in the user's favourites (default is false).
 * @param timeRegistration The list of the user's [TimeRegistration] for this project.
 */
data class ProjectDetails(
    val id: Long = -1L,
    val name: String = "",
    val description: String = "",
    val customer: Customer = Customer(),
    val startDate: LocalDate = Clock.System.todayIn(TimeZone.UTC),
    val endDate: LocalDate? = null,
    val workMinutesLeft: Long? = null,
    val tasks: List<Task> = emptyList(),
    var isStarred: Boolean = false,
    val timeRegistration: List<TimeRegistration> = emptyList(),
)

/**
 * Checks if the endDate is reached or if there are no more work minutes left.
 */
fun Project.isCompleted() =
    when {
        endDate != null -> this.endDate < LocalDate.now()
        workMinutesLeft != null -> this.workMinutesLeft <= 0
        else -> false
    }
