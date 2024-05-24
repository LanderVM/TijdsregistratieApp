package world.inetumrealdolmen.mobiletrm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimerEntry
import world.inetumrealdolmen.mobiletrm.data.local.entities.FilterPreferences
import world.inetumrealdolmen.mobiletrm.data.local.entities.SnackbarPreferences

/**
 * DAO to query with for user preferences in [LocalDatabase].
 */
@Dao
interface PreferencesDao {
    /**
     * Fetch the user's filter preferences
     *
     * @return the user's preferences
     */
    @Query("SELECT * FROM filter_preferences")
    suspend fun getFilterPreferences(): FilterPreferences?

    /**
     * Update the user's filter preferences.
     */
    @Insert(onConflict = REPLACE)
    suspend fun updateFilterPreferences(filterPreferences: FilterPreferences)

    /**
     * Fetch the app's snackbar preferences.
     *
     * @return the app's snackbar preferences
     */
    @Query("SELECT * FROM snackbar_preferences")
    suspend fun getSnackbarPreferences(): SnackbarPreferences?

    /**
     * Update the app's snackbar preferences.
     */
    @Insert(onConflict = REPLACE)
    suspend fun updateSnackbarPreferences(snackbarPreferences: SnackbarPreferences)

    /**
     * Get the list of time entries for the timer.
     */
    @Query("SELECT * FROM timer_entry ORDER BY id ASC")
    fun getTimerEntries(): Flow<List<DbTimerEntry>>

    /**
     * Add or update a time entry for the timer.
     */
    @Insert(onConflict = REPLACE)
    suspend fun addTimerEntry(entry: DbTimerEntry)

    /**
     * Delete all time entries for the timer.
     */
    @Query("DELETE FROM timer_entry")
    suspend fun deleteTimerEntries()
}
