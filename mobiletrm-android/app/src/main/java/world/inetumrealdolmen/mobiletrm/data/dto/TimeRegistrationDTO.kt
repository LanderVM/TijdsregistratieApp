package world.inetumrealdolmen.mobiletrm.data.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration

/**
 * Detailed information of a time registration from the api.
 *
 * @param id The database ID of the time registration.
 * @param description The optional description of the time registration.
 * @param startTime The start time of the time registration.
 * @param endTime The end time of the time registration.
 * @param assignedTask The details of the optional task the time registration is for.
 * @param assignedProject The details of the project the time registration is for.
 * @param tags The list of optional tags for the time registration.
 */
@Serializable
data class TimeRegistrationDTO(
    val id: Long,
    val description: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val assignedTask: TaskDTO?,
    val assignedProject: ProjectDTO.IndexShort?,
    val tags: List<TagDTO>,
)

/**
 * Request body to send to the api to create a new time registration.
 *
 * @param description The optional description of the time registration.
 * @param startTime The start time of the time registration.
 * @param endTime The end time of the time registration.
 * @param assignedProjectId The database ID of the project the time registration is for.
 * @param assignedTaskId The database ID of the optional task the time registration is for.
 * @param tags The list of optional tags for the time registration.
 */
@Serializable
data class TimeRegistrationCreateDTO(
    val description: String = "",
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val assignedProjectId: Long,
    val assignedTaskId: Long? = null,
    val tags: List<TagDTO>? = listOf(),
)

/**
 * Request body to send to the api to update an existing time registration.
 *
 * @param id The database ID of the time registration.
 * @param description The optional description of the time registration.
 * @param startTime The start time of the time registration.
 * @param endTime The end time of the time registration.
 * @param assignedProjectId The database ID of the project the time registration is for.
 * @param assignedTaskId The database ID of the optional task the time registration is for.
 * @param tags The list of optional tags for the time registration.
 */
@Serializable
data class TimeRegistrationUpdateDTO(
    val id: Long? = null,
    val description: String = "",
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val assignedProjectId: Long,
    val assignedTaskId: Long? = null,
    val tags: List<TagDTO> = listOf(),
)

/**
 * Converts the response data to the [TimeRegistration] model.
 */
fun TimeRegistrationDTO.asDomainObject(): TimeRegistration =
    TimeRegistration(
        remoteId = id,
        description = description,
        startTime = startTime,
        endTime = endTime,
        assignedTask = assignedTask?.asDomainObject(),
        assignedProject = assignedProject,
        tags = tags.asDomainObjects(),
    )

/**
 * Converts the [TimeRegistration] model to a Create DTO to persist to the API.
 *
 */
fun TimeRegistration.asDto() =
    TimeRegistrationCreateDTO(
        description = description ?: "",
        startTime = startTime,
        endTime = endTime,
        assignedProjectId = assignedProject?.id ?: -1,
        assignedTaskId = assignedTask?.taskId,
        tags = tags.map { tag -> tag.asDto() },
    )
