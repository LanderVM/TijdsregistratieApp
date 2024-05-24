package world.inetumrealdolmen.mobiletrm.data.dto

import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.serialization.Serializable
import world.inetumrealdolmen.mobiletrm.data.model.Tag

/**
 * Index information of a tag from the api.
 *
 * @param name The tag itself.
 */
@Serializable
data class TagDTO(
    val name: String,
)

/**
 * Converts the response data to a list of [Tag]
 */
fun List<TagDTO>.asDomainObjects() = map { it.asDomainObject() }

/**
 * Converts the response data to the [Tag] model.
 */
fun TagDTO.asDomainObject() = Tag(name = name)

/**
 * Converts the a [Tag] to a DTO to send to the api.
 */
fun Tag.asDto() = TagDTO(name = name)

/**
 * Converts a list of [Tag] to a list of DTOs to send to the api.
 */
fun ImmutableCollection<Tag>.asDtos() = map { it.asDto() }
