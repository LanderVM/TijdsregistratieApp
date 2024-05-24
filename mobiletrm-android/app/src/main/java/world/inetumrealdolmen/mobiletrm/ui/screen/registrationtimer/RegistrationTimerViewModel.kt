package world.inetumrealdolmen.mobiletrm.ui.screen.registrationtimer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import world.inetumrealdolmen.mobiletrm.data.model.ApiCrudState
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.CrudType
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType
import world.inetumrealdolmen.mobiletrm.data.model.RegistrationTimerState
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import world.inetumrealdolmen.mobiletrm.data.repository.impl.PreferencesRepositoryImpl
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.util.now
import world.inetumrealdolmen.mobiletrm.ui.util.parseException
import world.inetumrealdolmen.mobiletrm.ui.util.parsePutException
import javax.inject.Inject

@HiltViewModel
class RegistrationTimerViewModel
    @Inject
    constructor(
        private val repository: QuarkusRepository,
        private val preferences: PreferencesRepositoryImpl,
    ) :
    ViewModel() {
        // Status for retrieving timer entries
        private var viewModelState: ApiState by mutableStateOf(ApiState.Loading)

        // Status for manipulating the timer
        var timerState: ApiCrudState by mutableStateOf(ApiCrudState.Start)

        // Status for creating time registration concept
        var apiCrudState: ApiCrudState by mutableStateOf(ApiCrudState.Start)

        // List of entries in the timer
        private lateinit var timeEntriesState: StateFlow<List<TimeEntry>>
        private val timeEntries = MutableStateFlow(emptyList<TimeEntry>())

        // Timer's current state
        private val _uiState =
            MutableStateFlow(RegistrationTimerState())
        val uiState = _uiState.asStateFlow()

        companion object {
            const val MAX_PAUSES = 3
        }

        init {
            loadInitialTime()
            updateElapsedTime()
        }

        fun putTimeRegistrations() {
            apiCrudState = ApiCrudState.Loading

            viewModelScope.launch {
                apiCrudState =
                    try {
                        val dtos =
                            timeEntries.value.map {
                                TimeRegistration(
                                    startTime = LocalDateTime(LocalDate.now(), it.startTime),
                                    endTime =
                                        LocalDateTime(
                                            LocalDate.now(),
                                            if (it.endTime ==
                                                LocalTime(
                                                    0,
                                                    0,
                                                )
                                            ) {
                                                LocalTime.now()
                                            } else {
                                                it.endTime
                                            },
                                        ),
                                    assignedProject = null,
                                    assignedTask = null,
                                    description = null,
                                )
                            }

                        // Create registrations
                        repository.createTimeRegistration(dtos)
                        preferences.setSnackbarPreferences(
                            preferences.getSnackbarPreferences().copy(timeRegistrationCreated = true),
                        )

                        // Reset timer state
                        preferences.deleteTimerEntries()
                        ApiCrudState.Success(CrudType.Create)
                    } catch (e: Exception) {
                        parsePutException(e, "putTimerRegistration")
                    }
            }
        }

        private fun loadInitialTime() {
            viewModelState = ApiState.Loading
            timerState = ApiCrudState.Start
            viewModelScope.launch {
                try {
                    // Fetch timer entries
                    timeEntriesState =
                        preferences.getTimerEntries().stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5_000L),
                            initialValue = listOf(TimeEntry(-99)),
                        )

                    // Skip initial value
                    timeEntriesState.drop(1).collect { list ->
                        viewModelState = ApiState.Success

                        // Update timer state and elapsed time
                        timeEntries.update { list }
                        _uiState.update {
                            it.copy(
                                canPause =
                                    if (timeEntries.value.isEmpty()) {
                                        false
                                    } else {
                                        timeEntries.value.last().endTime ==
                                            LocalTime(
                                                0,
                                                0,
                                            )
                                    },
                                pauses =
                                    timeEntries.value.count { entry ->
                                        entry.endTime !=
                                            LocalTime(
                                                0,
                                                0,
                                            )
                                    },
                                canDiscard = timeEntries.value.isNotEmpty(),
                                canSubmit = timeEntries.value.isNotEmpty(),
                                elapsedTime =
                                    if (timeEntries.value.isEmpty()) {
                                        0
                                    } else {
                                        timeEntries.value.sumOf { entry ->
                                            val startTimeMillis = entry.startTime.toMillisecondOfDay()
                                            val endTimeMillis = entry.endTime.toMillisecondOfDay()

                                            (
                                                if (endTimeMillis == 0) {
                                                    LocalTime.now()
                                                        .toMillisecondOfDay()
                                                } else {
                                                    endTimeMillis
                                                }
                                            ) - startTimeMillis
                                        }
                                    },
                            )
                        }
                    }
                } catch (e: Exception) {
                    viewModelState = parseException(e, "fetchTimerEntries")
                }
            }
        }

        private fun updateElapsedTime() {
            viewModelScope.launch {
                while (isActive) {
                    // Add a second to the elapsed time while the timer is running
                    if (timeEntries.value.isNotEmpty() && timeEntries.value.last().endTime ==
                        LocalTime(0, 0)
                    ) {
                        _uiState.update { it.copy(elapsedTime = it.elapsedTime + 1000) }
                    }
                    delay(1000)
                }
            }
        }

        fun startTimer() {
            timerState = ApiCrudState.Loading
            viewModelScope.launch {
                // First start
                if (timeEntries.value.isEmpty()) {
                    preferences.addTimerEntry(TimeEntry(startTime = LocalTime.now()))
                    _uiState.update { it.copy(canDiscard = true, canPause = true, canSubmit = true) }
                    ApiCrudState.Start
                } else {
                    val lastEntry = timeEntries.value.last()

                    when {
                        // Timer was paused
                        lastEntry.endTime != LocalTime(0, 0) ->
                            timerState =
                                when (timeEntries.value.count() <= MAX_PAUSES) {
                                    // Max amount of pauses has been achieved
                                    false ->
                                        ApiCrudState.Error(
                                            ErrorType.Unknown("Max amount of pauses has been achieved!\nPlease submit the timer."),
                                        )
                                    // Start timer again
                                    true -> {
                                        preferences.addTimerEntry(TimeEntry(startTime = LocalTime.now()))
                                        _uiState.update {
                                            it.copy(
                                                canDiscard = true,
                                                canPause = false,
                                                canSubmit = true,
                                            )
                                        }
                                        ApiCrudState.Start
                                    }
                                }

                        // Timer wasn't paused when it should've been
                        else ->
                            timerState =
                                ApiCrudState.Error(
                                    ErrorType.Unknown(
                                        "Timer wasn't paused when it should've been!\nPlease discard the timer and try again.",
                                    ),
                                )
                    }
                }
            }
        }

        fun pauseTimer() {
            timerState = ApiCrudState.Loading
            viewModelScope.launch {
                // First start
                if (timeEntries.value.isEmpty()) {
                    timerState =
                        ApiCrudState.Error(
                            ErrorType.Unknown("Timer hasn't started while it should've been!\nPlease discard the timer and try again."),
                        )
                } else {
                    val lastEntry = timeEntries.value.last()
                    timerState =
                        when {
                            // Timer is running, update time entry
                            lastEntry.endTime == LocalTime(0, 0) -> {
                                preferences.addTimerEntry(lastEntry.apply { endTime = LocalTime.now() })
                                _uiState.update {
                                    it.copy(
                                        canDiscard = true,
                                        canPause = false,
                                        pauses = it.pauses + 1,
                                    )
                                }
                                ApiCrudState.Start
                            }

                            // Timer was already paused when it shouldn't have been
                            else ->
                                ApiCrudState.Error(
                                    ErrorType.Unknown("Timer wasn't paused when it should've been!\nPlease try restarting the timer."),
                                )
                        }
                }
            }
        }

        fun discardCurrentTimer() {
            timerState = ApiCrudState.Loading
            viewModelScope.launch {
                preferences.deleteTimerEntries()
            }
            _uiState.update { it.copy(elapsedTime = 0) }
        }
    }
