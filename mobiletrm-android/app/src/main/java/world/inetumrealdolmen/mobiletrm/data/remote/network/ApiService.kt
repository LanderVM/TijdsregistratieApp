package world.inetumrealdolmen.mobiletrm.data.remote.network

import kotlinx.datetime.LocalDate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import world.inetumrealdolmen.mobiletrm.data.dto.ProjectDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeEntryDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationCreateDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationUpdateDTO

/**
 * Defines API service endpoints.
 */
interface ApiService {
    /**
     * Fetches projects data from the api.
     */
    @GET("/api/projects/")
    suspend fun getProjects(): List<ProjectDTO.Index>

    /**
     * Fetches details from a specific project using its ID to query.
     *
     * @param id The database ID to query for.
     */
    @GET("/api/projects/{id}")
    suspend fun getProjectDetails(
        @Path("id") id: Long,
    ): ProjectDTO.Details

    /**
     * (Un)favorites a project.
     *
     * @param id The database ID to query for.
     * @param isFavorite Whether the project should be favorited or unfavorited.
     */
    @PUT("/api/projects/{id}/favorite")
    suspend fun setFavorite(
        @Path("id") id: Long,
        @Body isFavorite: ProjectDTO.UpdateFavorite,
    ): Boolean

    /**
     * Temporary implementation, replace once authentication is in
     */
    @GET("/api/projects/tasks")
    suspend fun getProjectDetails(): List<ProjectDTO.Tasks>

    /**
     * Creates a new time registration for a project.
     *
     * @param timeRegistrationDTO the details of the time registration to persist.
     */
    @POST("/api/time-registrations")
    suspend fun createTimeRegistration(
        @Body timeRegistrationDTO: List<TimeRegistrationCreateDTO>,
    )

    /**
     * Fetches time registrations data from the api.
     */
    @GET("/api/time-registrations")
    suspend fun getTimeRegistrations(): List<TimeRegistrationDTO>

    /**
     * Fetches details from a specific time registration using its ID to query.
     *
     * @param id The database ID to query for.
     */
    @GET("/api/time-registrations/{id}")
    suspend fun getTimeRegistration(
        @Path("id") id: Long,
    ): TimeRegistrationDTO

    /**
     * Deletes a specific time registration based on its ID.
     *
     * @param id The database ID to query for.
     */
    @DELETE("/api/time-registrations/{id}")
    suspend fun deleteTimeRegistrationById(
        @Path("id") id: Long,
    )

    /**
     * Updates an existing time registration for a project.
     *
     * @param timeRegistrationDTO the details of the time registration to persist.
     */
    @PUT("/api/time-registrations")
    suspend fun updateTimeRegistration(
        @Body timeRegistrationDTO: TimeRegistrationUpdateDTO,
    )

    @GET("/api/time-registrations/time-entries/{date}")
    suspend fun getTimeEntries(
        @Path("date") date: LocalDate,
    ): List<TimeEntryDTO>
}
