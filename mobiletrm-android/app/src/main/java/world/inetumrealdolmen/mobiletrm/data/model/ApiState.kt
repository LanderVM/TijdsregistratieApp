package world.inetumrealdolmen.mobiletrm.data.model

import kotlinx.serialization.Serializable

/**
 * Represents the different states of an API GET call.
 */
sealed interface ApiState {
    /**
     * Indicates a successful API response.
     */
    data object Success : ApiState

    /**
     * Indicates an error in the API response.
     *
     * @param details The details of the prompted error to display to the user
     */
    data class Error(val details: ErrorType) : ApiState

    /**
     * Indicates that the API call is currently loading.
     */
    data object Loading : ApiState
}

/**
 * Represents the different states of an API CRUD state, excluding Read.
 */
sealed interface ApiCrudState {
    /**
     * Clean state where the api call hasn't been sent yet.
     */
    data object Start : ApiCrudState

    /**
     * Indicates a successful API response.
     */
    data class Success(val type: CrudType) : ApiCrudState

    /**
     * Indicates an error in the API response.
     *
     * @param details The details of the prompted error to display to the user
     */
    data class Error(val details: ErrorType) : ApiCrudState

    /**
     * Indicates that the API call is currently being handled.
     */
    data object Loading : ApiCrudState
}

/**
 * The type of API call that was made, used in [ApiCrudState.Success].
 */
sealed interface CrudType {
    /**
     * The request was of type POST.
     */
    data object Create : CrudType

    /**
     * The request was of type PUT.
     */
    data object Update : CrudType

    /**
     * The request was of type DELETE.
     */
    data object Delete : CrudType
}

/**
 * Represents the cause of why an api call failed.
 */
@Serializable
sealed interface ErrorType {
    /**
     * The requested resource could not be found.
     */
    @Serializable
    data object NotFound : ErrorType

    /**
     * The request can not be processed due to a client error.
     */
    @Serializable
    data object BadRequest : ErrorType

    /**
     * The connection with the server failed.
     */
    @Serializable
    data object SocketTimeout : ErrorType

    /**
     * An internal server error occurred.
     */
    @Serializable
    data object Internal : ErrorType

    /**
     * An unknown error occurred.
     * @param errorMessage The response from the server.
     */
    @Serializable
    data class Unknown(val errorMessage: String?) : ErrorType

    /**
     * An unmapped error occurred.
     */
    @Serializable
    data object Unmapped : ErrorType
}
