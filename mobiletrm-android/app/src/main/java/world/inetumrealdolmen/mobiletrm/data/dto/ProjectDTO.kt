package world.inetumrealdolmen.mobiletrm.data.dto

import androidx.room.ColumnInfo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetails
import world.inetumrealdolmen.mobiletrm.data.model.ProjectTasks

/**
 * Collection of DTOs regarding projects from the api.
 */
class ProjectDTO {
    /**
     * Index information of a project.
     * A project has either an [endDate] or an amount of [workMinutesLeft].
     *
     * @param id The database ID of the project.
     * @param name The name of the project.
     * @param endDate The end date of the project (nullable).
     * @param workMinutesLeft The remaining work time in minutes (nullable).
     * @param isFavorite Whether the project is in the user's list of favorites.
     * @param whenFavorited When the project was last (un)favorited.
     */
    @Serializable
    data class Index(
        val id: Long,
        val name: String,
        val endDate: LocalDate?,
        val workMinutesLeft: Long?,
        val isFavorite: Boolean,
        val whenFavorited: LocalDateTime,
    )

    /**
     * Shorter index information of a project containing only its name.
     *
     * @param id The database ID of the project.
     * @param name The name of the project.
     */
    @Serializable
    data class IndexShort(
        @ColumnInfo(name = "project_id") val id: Long,
        @ColumnInfo(name = "project_name") val name: String,
    )

    /**
     * Shorter index information of a project including the project's list of tasks.
     *
     * @param id The database ID of the project.
     * @param name The name of the project.
     * @param tasks The list of available [world.inetumrealdolmen.mobiletrm.data.model.Task] of the project.
     */
    @Serializable
    data class Tasks(
        val id: Long,
        val name: String,
        val tasks: List<TaskDTO>,
    )

    /**
     * Detailed information of a project.
     *
     * @param id The database ID of the project.
     * @param name The name of the project.
     * @param description The description of the project.
     * @param customer The customer of the project.
     * @param startDate The start date of the project.
     * @param endDate The end date of the project (nullable).
     * @param workMinutesLeft The remaining work time in minutes (nullable).
     * @param tasks The list of available tasks belonging to this project.
     * @param timeRegistration The list of existing time registrations for this project.
     */
    @Serializable
    data class Details(
        val id: Long,
        val name: String,
        val description: String,
        val customer: CustomerDTO,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val workMinutesLeft: Long?,
        val tasks: List<TaskDTO>,
        val timeRegistration: List<TimeRegistrationDTO>,
    )

    /**
     * Represents the body of a PUT operation to mark a project as (un)favorited.
     *
     * @param isFavorite whether the project should be favorited or unfavorited.
     */
    @Serializable
    data class UpdateFavorite(val isFavorite: Boolean)
}

/**
 * Converts the response data to the [ProjectDetails] model.
 */
fun ProjectDTO.Details.asDomainObject(): ProjectDetails =
    ProjectDetails(
        id = id,
        name = name,
        description = description,
        customer = customer.asDomainObject(),
        startDate = startDate,
        endDate = endDate,
        workMinutesLeft = workMinutesLeft,
        tasks = tasks.asDomainObjects(),
        isStarred = false,
        timeRegistration = timeRegistration.map { it.asDomainObject() },
    )

/**
 * Converts the response data to a list of [Project] models.
 */
fun List<ProjectDTO.Index>.asDomainObjects(): List<Project> =
    map {
        Project(
            id = it.id,
            name = it.name,
            endDate = it.endDate,
            workMinutesLeft = it.workMinutesLeft,
            isFavorite = it.isFavorite,
            whenFavorited = it.whenFavorited,
        )
    }

/**
 * Converts the response data to a list of [ProjectTasks] models.
 */
fun List<ProjectDTO.Tasks>.asDomainTaskObjects(): List<ProjectTasks> =
    map {
        ProjectTasks(
            id = it.id,
            name = it.name,
            tasks = it.tasks.asDomainObjects(),
        )
    }
