package world.inetumrealdolmen.mobiletrm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbProject
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbProjectDetails
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimeRegistration
import world.inetumrealdolmen.mobiletrm.data.local.entities.ProjectWithDetails
import java.util.UUID

/**
 * DAO to query with (cached) data related to the Mobile TRM api in [LocalDatabase].
 */
@Dao
interface MobileTRMDao {
    /**
     * Fetch all time registrations as a hot flow that updates whenever the database changes.
     *
     * @return A [Flow] of [DbTimeRegistration] objects.
     */
    @Query("SELECT * FROM time_registration")
    fun getAllRegistrations(): Flow<List<DbTimeRegistration>>

    /**
     * Fetch all current concept time registrations that should be persisted to the api.
     *
     * @return A [List] of [DbTimeRegistration] objects to sync.
     */
    @Query("SELECT * FROM time_registration WHERE remote_registration_id is null AND project_id is not null AND syncing_retries <= 3")
    fun getUnsyncedRegistrations(): List<DbTimeRegistration>

    /**
     * Fetch all projects as a hot flow that updates whenever the database changes.
     *
     * @return A [Flow] of [DbProject] objects.
     */
    @Query("SELECT * FROM project")
    fun getAllProjects(): Flow<List<DbProject>>

    /**
     * Fetch a project with all its details by its ID.
     *
     * @return A [ProjectWithDetails] object.
     */
    @Transaction
    @Query("SELECT * FROM project WHERE project_id = :projectId")
    fun getProjectDetails(projectId: Long): ProjectWithDetails?

    /**
     * Inserts a concept time registration into the database.
     *
     * @param item The [DbTimeRegistration] object to be inserted.
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(item: DbTimeRegistration)

    /**
     * Inserts a list of projects into the database, overwriting old data.
     *
     * @param item The list of [DbProject] objects to be inserted.
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(item: List<DbProject>)

    /**
     * Inserts a project's details into the database.
     *
     * @param item The [DbProjectDetails] object to be inserted.
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(item: DbProjectDetails)

    /**
     * Removes a concept time registration from the database.
     *
     * @param item The [DbTimeRegistration] object to be deleted.
     */
    @Delete
    suspend fun delete(item: DbTimeRegistration)

    /**
     * Removes a concept time registration from the database.
     *
     * @param id The ID of the [DbTimeRegistration] object to be deleted.
     */
    @Query("DELETE FROM time_registration WHERE local_registration_id = :id")
    suspend fun delete(id: UUID)

    /**
     * Fetches a time registration by its locally stored ID.
     */
    @Query("SELECT * FROM time_registration WHERE local_registration_id = :id")
    suspend fun getRegistrationByLocalId(id: UUID): DbTimeRegistration

    /**
     * Fetches a time registration by its remotely stored ID.
     */
    @Query("SELECT * FROM time_registration WHERE remote_registration_id = :id")
    suspend fun getRegistrationByRemoteId(id: Long): DbTimeRegistration?

    /**
     * Finds a project by its ID and updates its favorite boolean.
     *
     * @param id The ID to query for.
     * @param result The new favorite value.
     */
    @Query("UPDATE project SET is_favorite = :result WHERE project_id = :id")
    fun setFavorite(
        id: Long,
        result: Boolean,
    )

    /**
     * Get all the user's favorited projects.
     */
    @Query("SELECT * FROM project WHERE is_favorite = true")
    fun getFavoriteProject(): DbProject?
}
