package world.inetumrealdolmen.mobiletrm.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetails
import world.inetumrealdolmen.mobiletrm.data.model.ProjectTasks
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import java.util.UUID

/**
 * Repository interface for managing project data.
 */
interface MobileTRMRepository {
    /**
     * Fetches the user's projects as a hot-flow that updates every time the data changes.
     * The data updates when a change in the local cache is observed.
     */
    suspend fun getProjects(): Flow<List<Project>>

    /**
     * Fetches details of a specific project using its ID to query.
     *
     * @param id The database ID to query for.
     */
    suspend fun getProjectDetails(id: Long): ProjectDetails?

    /**
     * Fetches all projects with their tasks.
     */
    suspend fun getProjectTasks(): List<ProjectTasks>

    /**
     * Fetches the list of IDs of the user's current running projects
     */
    suspend fun getRunningProjectIds(): List<Long>

    /**
     * (Un)favorites a project.
     *
     * @param id The database ID to query for.
     * @param isFavorite Whether the project should be favorited or unfavorited.
     * @return The new favorite state of the project.
     */
    suspend fun favoriteProject(
        id: Long,
        isFavorite: Boolean,
    )

    /**
     * Gets the latest favorited project.
     *
     * @return The latest favorited project.
     */
    suspend fun getFavoritedProject(): Project?

    /**
     * Creates a new time registration with every time entry being an individual registration.
     * If the time registration has no [TimeRegistration.assignedProject], it will be persisted as a concept.
     * If there is an [TimeRegistration.assignedProject] but there is no internet connection, it will be persisted as a concept.
     *
     * @param timeRegistration The details of the new time entries to persist.
     */
    suspend fun createTimeRegistration(timeRegistration: List<TimeRegistration>)

    /**
     * Fetches the user's time registrations as a hot-flow that updates every time the data changes.
     * The data updates when a change in the local cache is observed.
     */
    suspend fun getTimeRegistrations(): Flow<List<TimeRegistration>>

    /**
     * Get the user's existing time entries for a specific day.
     *
     * @param date The date to fetch time entries for.
     */
    suspend fun getTimeEntries(date: LocalDate): List<TimeEntry>

    /**
     * Fetches a specific time registration by its local ID.
     *
     * @param id The database ID to query for.
     */
    suspend fun getTimeRegistration(id: UUID): TimeRegistration

    /**
     * Deletes a time registration using its database ID.
     *
     * @param localId The local database ID to query for.
     */
    suspend fun deleteTimeRegistrationById(localId: UUID)

    /**
     * Updates a time registration by its remote id.
     * If there is no internet connection, it will be updated as a concept.
     *
     * @param timeRegistration The details of the time registration to persist.
     */
    suspend fun updateTimeRegistration(timeRegistration: TimeRegistration)
}
