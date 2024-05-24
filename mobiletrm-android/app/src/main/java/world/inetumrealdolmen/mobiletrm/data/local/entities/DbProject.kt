package world.inetumrealdolmen.mobiletrm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import world.inetumrealdolmen.mobiletrm.data.model.Project

/**
 * A [Project] database entity.
 */
@Entity(tableName = "project")
data class DbProject(
    @PrimaryKey
    @ColumnInfo(name = "project_id")
    val projectId: Long = -1L,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "end_date")
    val endDate: LocalDate? = null,
    @ColumnInfo(name = "work_minutes_left")
    val workMinutesLeft: Long? = null,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false,
)

/**
 * Converts a project to a model object, used to display or persist.
 *
 * @return A Project model.
 */
fun DbProject.asDomainObject() =
    Project(
        id = projectId,
        name = name,
        endDate = endDate,
        workMinutesLeft = workMinutesLeft,
        isFavorite = isFavorite,
    )

/**
 * Converts a project to a database concept registration.
 *
 * @return A database entity.
 */
fun Project.asDbObject() =
    DbProject(
        projectId = id,
        name = name,
        endDate = endDate,
        workMinutesLeft = workMinutesLeft,
        isFavorite = isFavorite,
    )
