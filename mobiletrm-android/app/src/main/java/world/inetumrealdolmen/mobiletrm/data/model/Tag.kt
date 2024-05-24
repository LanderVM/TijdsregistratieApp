package world.inetumrealdolmen.mobiletrm.data.model

/**
 * The model of a tag.
 *
 * @param id The database ID of the tag. -1 if it hasn't been persisted to/from the database.
 * @param name The tag itself.
 */
data class Tag(
    val id: Long = -1L,
    val name: String = "",
)
