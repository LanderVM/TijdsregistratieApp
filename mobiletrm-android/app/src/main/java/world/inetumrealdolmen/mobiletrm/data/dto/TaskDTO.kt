package world.inetumrealdolmen.mobiletrm.data.dto

import kotlinx.serialization.Serializable
import world.inetumrealdolmen.mobiletrm.data.model.Task

/**
 * Index information of a task.
 *
 * @param id The database ID of the task.
 * @param name The name of the task.
 */
@Serializable
data class TaskDTO(
    var id: Long,
    var name: String,
)

/**
 * Converts the response data to a list of [Task]
 */
fun List<TaskDTO>.asDomainObjects(): List<Task> =
    map {
        it.asDomainObject()
    }

/**
 * Converts the response data to the [Task] model.
 */
fun TaskDTO.asDomainObject(): Task =
    Task(
        taskId = id,
        taskName = name,
    )
