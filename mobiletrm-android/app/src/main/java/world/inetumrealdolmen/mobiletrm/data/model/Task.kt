package world.inetumrealdolmen.mobiletrm.data.model

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

/**
 * The model of a Task's index.
 *
 * @param taskId The database ID of the task.
 * @param taskName The name of the task.
 */
@Serializable
data class Task(
    @ColumnInfo(name = "task_id")
    val taskId: Long = -1L,
    @ColumnInfo(name = "task_name")
    val taskName: String = "",
)

/**
 * The list of tasks per [Project].
 *
 * @param id The database ID of the project.
 * @param name The name of the project.
 * @param tasks The list of [Task] associated with the project.
 */
data class ProjectTasks(
    val id: Long = -1L,
    val name: String = "",
    val tasks: List<Task> = emptyList(),
)

/**
 * Maps a [ProjectTasks] object to a [Project] object containing the ID and name of the project.
 * All other values will be default.
 */
fun ProjectTasks.asProject() = Project(id = id, name = name)
