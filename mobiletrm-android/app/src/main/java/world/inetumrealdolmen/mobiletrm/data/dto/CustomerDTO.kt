package world.inetumrealdolmen.mobiletrm.data.dto

import kotlinx.serialization.Serializable
import world.inetumrealdolmen.mobiletrm.data.model.Customer

/**
 * Index information about the customer from the api.
 *
 * @param id The database ID of the customer.
 * @param name The full name of the customer.
 * @param companyName The name of the company that employs the customer.
 */
@Serializable
data class CustomerDTO(
    val id: Long,
    val name: String,
    val companyName: String,
)

/**
 * Maps a Customer received from the API to a model object.
 *
 * @return The model object of the Customer.
 */
fun CustomerDTO.asDomainObject() =
    Customer(
        id = id,
        name = name,
        companyName = companyName,
    )

/**
 * Maps a Customer modele to a DTO.
 *
 * @return The DTO of the Customer.
 */
fun Customer.asDto() =
    CustomerDTO(
        id = id,
        name = name,
        companyName = companyName,
    )
