package world.inetumrealdolmen.mobiletrm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import world.inetumrealdolmen.mobiletrm.data.dto.CustomerDTO
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.dto.asDto
import world.inetumrealdolmen.mobiletrm.data.model.Customer
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetails
import world.inetumrealdolmen.mobiletrm.data.model.Task

/**
 * A [ProjectDetails] database entity.
 */
@Entity(tableName = "project_details")
data class DbProjectDetails(
    @PrimaryKey
    @ColumnInfo(name = "details_id")
    val detailsId: Long = -1L,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate = Clock.System.todayIn(TimeZone.UTC),
    val tasks: List<Task> = emptyList(),
    @Embedded val customer: CustomerDTO,
)

/**
 * Defines a one-to-one relationship between [DbProject] and its [DbProjectDetails] and [DbTimeRegistration]s.
 */
data class ProjectWithDetails(
    @Embedded val project: DbProject,
    @Relation(
        parentColumn = "project_id",
        entityColumn = "details_id",
    )
    val details: DbProjectDetails?,
    @Relation(
        parentColumn = "project_id",
        entityColumn = "project_id",
    )
    val timeRegistrations: List<DbTimeRegistration>,
)

/**
 * Converts a project with its details to a model object, used to display or persist.
 * If no [DbProjectDetails.customer] or [DbProjectDetails.startDate] have been provided, a new one will be made.
 *
 * @return A ProjectDetails model.
 */
fun ProjectWithDetails.asDomainObject(): ProjectDetails =
    ProjectDetails(
        id = project.projectId,
        name = project.name,
        description = details?.description ?: "",
        customer = details?.customer?.asDomainObject() ?: Customer(),
        startDate = details?.startDate ?: Clock.System.todayIn(TimeZone.UTC),
        endDate = project.endDate,
        workMinutesLeft = project.workMinutesLeft,
        tasks = details?.tasks ?: emptyList(),
        isStarred = project.isFavorite,
        timeRegistration = timeRegistrations.map { it.asDomainObject() },
    )

/**
 * Converts a project's details to a database entity.
 *
 * @return A database entity.
 */
fun ProjectDetails.asDbObject(): DbProjectDetails =
    DbProjectDetails(
        detailsId = id,
        description = description,
        startDate = startDate,
        tasks = tasks,
        customer = customer.asDto(),
    )
