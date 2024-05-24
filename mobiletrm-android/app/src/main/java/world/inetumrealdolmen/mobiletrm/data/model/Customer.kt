package world.inetumrealdolmen.mobiletrm.data.model

/**
 * Represents the customer of a [Project].
 *
 * @param id The ID of the customer.
 * @param name The name of the customer.
 * @param companyName The company associated with the customer.
 */
data class Customer(
    val id: Long = -1L,
    val name: String = "",
    val companyName: String = "",
)
