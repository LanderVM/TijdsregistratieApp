package world.inetumrealdolmen.mobiletrm.data.local.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import world.inetumrealdolmen.mobiletrm.data.local.PreferencesDao
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimerEntry
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.screen.registrationtimer.RegistrationTimerViewModel
import world.inetumrealdolmen.mobiletrm.ui.util.now

@HiltWorker
class BluetoothBeaconScanningWorker
    @AssistedInject
    constructor(
        @Assisted val context: Context,
        @Assisted params: WorkerParameters,
        private val repository: QuarkusRepository,
        private val preferences: PreferencesDao,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val action = inputData.getString("action")

            val success: Boolean =
                when (action) {
                    "start" -> startTimer()
                    "stop" -> createRegistration()
                    else -> false
                }

            return if (success) Result.success() else Result.failure()
        }

        private suspend fun createRegistration(): Boolean {
            val dtos =
                preferences.getTimerEntries().first().map {
                    TimeRegistration(
                        startTime = LocalDateTime(LocalDate.now(), it.startTime.time),
                        endTime =
                            LocalDateTime(
                                LocalDate.now(),
                                time =
                                    if (it.endTime.time == LocalTime(0, 0)) {
                                        LocalTime.now()
                                    } else {
                                        it.endTime.time
                                    },
                            ),
                        assignedProject = null,
                        assignedTask = null,
                        description = null,
                    )
                }

            if (dtos.isEmpty()) return false

            // Create registrations & delete the entries from local db
            repository.createTimeRegistration(dtos)
            preferences.deleteTimerEntries()
            return true
        }

        private suspend fun startTimer(): Boolean {
            val entries = preferences.getTimerEntries().first()

            if (entries.isEmpty()) {
                preferences.addTimerEntry(DbTimerEntry(startTime = LocalDateTime(LocalDate.now(), LocalTime.now())))
                return true
            } else {
                val lastEntry = entries.last()

                // Timer was paused
                return when {
                    lastEntry.endTime.time != LocalTime(0, 0) -> {
                        if (entries.count() >= RegistrationTimerViewModel.MAX_PAUSES) {
                            // Max amount of pauses has been achieved
                            return false
                        }
                        preferences.addTimerEntry(DbTimerEntry(startTime = LocalDateTime(LocalDate.now(), LocalTime.now())))
                        true
                    }
                    // Timer wasn't paused when it should've been
                    else -> false
                }
            }
        }
    }
