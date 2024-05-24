package world.inetumrealdolmen.mobiletrm.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atDate
import world.inetumrealdolmen.mobiletrm.data.local.PreferencesDao
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimerEntry
import world.inetumrealdolmen.mobiletrm.data.local.entities.FilterPreferences
import world.inetumrealdolmen.mobiletrm.data.local.entities.SnackbarPreferences
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.data.repository.PreferencesRepository
import world.inetumrealdolmen.mobiletrm.ui.util.now
import javax.inject.Inject

/**
 * Implementation of [PreferencesRepository].
 *
 * @param dao The DAO for fetching data from local storage.
 */
class PreferencesRepositoryImpl
    @Inject
    constructor(
        private val dao: PreferencesDao,
    ) : PreferencesRepository {
        override suspend fun getFilterPreferences(): FilterPreferences = dao.getFilterPreferences() ?: FilterPreferences()

        override suspend fun setFilterPreferences(filterPreferences: FilterPreferences) = dao.updateFilterPreferences(filterPreferences)

        override suspend fun getSnackbarPreferences(): SnackbarPreferences = dao.getSnackbarPreferences() ?: SnackbarPreferences()

        override suspend fun setSnackbarPreferences(snackbarPreferences: SnackbarPreferences) =
            dao.updateSnackbarPreferences(snackbarPreferences)

        override suspend fun getTimerEntries(): Flow<List<TimeEntry>> {
            return dao.getTimerEntries().map { entry -> entry.map { TimeEntry(it.id, it.startTime.time, it.endTime.time) } }
        }

        override suspend fun addTimerEntry(timeEntry: TimeEntry) {
            dao.addTimerEntry(
                DbTimerEntry(
                    id = timeEntry.id,
                    startTime = timeEntry.startTime.atDate(LocalDate.now()),
                    endTime = timeEntry.endTime.atDate(LocalDate.now()),
                ),
            )
        }

        override suspend fun deleteTimerEntries() {
            dao.deleteTimerEntries()
        }
    }
