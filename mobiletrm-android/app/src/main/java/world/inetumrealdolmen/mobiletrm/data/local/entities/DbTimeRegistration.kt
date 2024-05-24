package world.inetumrealdolmen.mobiletrm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import world.inetumrealdolmen.mobiletrm.data.dto.ProjectDTO
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType
import world.inetumrealdolmen.mobiletrm.data.model.Tag
import world.inetumrealdolmen.mobiletrm.data.model.Task
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import java.util.UUID

/**
 * A concept [TimeRegistration] database entity.
 *
 * @param localId The local ID of the time registration.
 * @param remoteId The remote ID of the time registration, or null if it hasn't been persisted yet.
 * @param description The description of the time registration.
 * @param startTime The start time of the time registration.
 * @param endTime The end time of the time registration.
 * @param assignedTask The assigned task of the time registration, or null if there is no assigned task.
 * @param assignedProject The assigned project of the time registration, or null if it's still a concept.
 * @param tags The tags associated with the time registration.
 * @param syncingError The error that occurred during synchronisation, or null if it hasn't been synced yet.
 * @param syncingRetries The number of retries for syncing the time registration.
 */
@Entity(tableName = "time_registration")
data class DbTimeRegistration(
    @PrimaryKey
    @ColumnInfo(name = "local_registration_id")
    val localId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "remote_registration_id")
    val remoteId: Long?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "start_time")
    val startTime: LocalDateTime,
    @ColumnInfo(name = "end_time")
    val endTime: LocalDateTime,
    @Embedded val assignedTask: Task?,
    @Embedded val assignedProject: ProjectDTO.IndexShort?,
    val tags: List<String>,
    @ColumnInfo(name = "syncing_error_code")
    var syncingError: ErrorType? = null,
    @ColumnInfo(name = "syncing_retries")
    var syncingRetries: Int = 0,
)

/**
 * Converts the time registration concept to a model object, used to display or persist.
 *
 * @return A [TimeRegistration] model
 */
fun DbTimeRegistration.asDomainObject(): TimeRegistration =
    TimeRegistration(
        remoteId = remoteId ?: -1L,
        localId = localId,
        startTime = startTime,
        endTime = endTime,
        description = description,
        assignedTask = assignedTask,
        assignedProject = assignedProject,
        tags = tags.map { Tag(-1, it) },
        syncingError = syncingError,
    )

/**
 * Converts a time registration to a database entity.
 *
 * @return A [DbTimeRegistration] database entity.
 */
fun TimeRegistration.asDbObject(): DbTimeRegistration =
    DbTimeRegistration(
        remoteId = if (remoteId == -1L) null else remoteId,
        localId = localId,
        description = description,
        startTime = startTime,
        endTime = endTime,
        assignedTask = assignedTask,
        assignedProject = assignedProject,
        tags = tags.map { it.name },
        syncingError = syncingError,
    )
