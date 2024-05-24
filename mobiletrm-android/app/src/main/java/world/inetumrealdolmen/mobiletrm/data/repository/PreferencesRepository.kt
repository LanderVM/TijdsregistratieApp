package world.inetumrealdolmen.mobiletrm.data.repository

import kotlinx.coroutines.flow.Flow
import world.inetumrealdolmen.mobiletrm.data.local.entities.FilterPreferences
import world.inetumrealdolmen.mobiletrm.data.local.entities.SnackbarPreferences
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry

/**
 * Repository interface for managing the user's preferences and app-related data
 */
interface PreferencesRepository {
    /**
     * Fetches the user's preferences regarding the filter on the time registrations overview page.
     */
    suspend fun getFilterPreferences(): FilterPreferences

    /**
     * Updates the user's preferences regarding the filter on the time registrations overview page.
     */
    suspend fun setFilterPreferences(filterPreferences: FilterPreferences)

    /**
     * Fetches the user's preferences regarding the snackbar on the time registrations overview page.
     */
    suspend fun getSnackbarPreferences(): SnackbarPreferences

    /**
     * Updates the user's preferences regarding the snackbar on the time registrations overview page.
     */
    suspend fun setSnackbarPreferences(snackbarPreferences: SnackbarPreferences)

    /**
     * Fetches the time entries for the user's timer.
     */
    suspend fun getTimerEntries(): Flow<List<TimeEntry>>

    /**
     * Adds a new timer entry to the user's timer.
     */
    suspend fun addTimerEntry(timeEntry: TimeEntry)

    /**
     * Deletes all timer entries from the user's timer.
     */
    suspend fun deleteTimerEntries()
}
