package world.inetumrealdolmen.mobiletrm.ui.screen.timeregistrationsoverview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import world.inetumrealdolmen.mobiletrm.data.local.entities.FilterPreferences
import world.inetumrealdolmen.mobiletrm.data.local.entities.SnackbarPreferences
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistrationIndexState
import world.inetumrealdolmen.mobiletrm.data.repository.impl.PreferencesRepositoryImpl
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.util.now
import world.inetumrealdolmen.mobiletrm.ui.util.parseException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TimeRegistrationsOverviewViewModel
    @Inject
    constructor(
        private val repository: QuarkusRepository,
        private val userPreferences: PreferencesRepositoryImpl,
    ) :
    ViewModel() {
        var registrationsState: ApiState by mutableStateOf(ApiState.Loading)
        var filterState: ApiState by mutableStateOf(ApiState.Loading)
        private var apiDeleteState: ApiState by mutableStateOf(ApiState.Loading)

        private val _uiState = MutableStateFlow(TimeRegistrationIndexState())
        val uiState = _uiState.asStateFlow()

        private val _snackbar = MutableStateFlow(SnackbarPreferences())
        val snackbar: StateFlow<SnackbarPreferences> = _snackbar.asStateFlow()

        private lateinit var fromDb: StateFlow<List<TimeRegistration>?>

        private val _referenceDate = MutableStateFlow(LocalDate.now())
        val referenceDate: StateFlow<LocalDate> = _referenceDate

        private val _selectedFilter = MutableStateFlow(FilterType.Week)
        val selectedFilter: StateFlow<FilterType> = _selectedFilter.asStateFlow()

        private var offset: DatePeriod = DatePeriod()

        init {
            fetchFilterPreferences()
            fetchTimeRegistrations()
        }

        private fun fetchTimeRegistrations() {
            registrationsState = ApiState.Loading
            viewModelScope.launch {
                try {
                    // Fetch snackbar preferences
                    _snackbar.update { userPreferences.getSnackbarPreferences() }

                    // Fetch time registrations
                    fromDb =
                        repository.getTimeRegistrations().stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5_000L),
                            initialValue = null,
                        )

                    val runningProjects = repository.getRunningProjectIds()

                    // Don't count initial value as finished loading
                    fromDb.drop(1).collect {
                        val filteredResponse =
                            applyFilter(
                                it!!.toImmutableList(),
                                _selectedFilter.value,
                            )
                        _uiState.update { state ->
                            state.copy(timeRegistrations = filteredResponse, runningProjectIds = runningProjects.toImmutableList())
                        }

                        registrationsState = ApiState.Success
                    }
                } catch (e: Exception) {
                    registrationsState = parseException(e, "fetchTimeRegistrations")
                }
            }
        }

        fun resetSnackbar() {
            viewModelScope.launch {
                userPreferences.setSnackbarPreferences(SnackbarPreferences())
            }
        }

        private fun fetchFilterPreferences() {
            filterState = ApiState.Loading
            viewModelScope.launch {
                try {
                    val filter = userPreferences.getFilterPreferences()
                    _referenceDate.update { filter.referenceDate }
                    _selectedFilter.update { filter.selectedFilter }
                    offset = filter.offset
                    filterState = ApiState.Success
                } catch (e: Exception) {
                    registrationsState = parseException(e, "fetchTimeRegistrations")
                }
            }
        }

        fun deleteTimeRegistration(localId: UUID) {
            apiDeleteState = ApiState.Loading
            viewModelScope.launch {
                try {
                    repository.deleteTimeRegistrationById(localId)
                    apiDeleteState = ApiState.Success
                } catch (e: Exception) {
                    apiDeleteState = parseException(e, "deleteTimeRegistration")
                }
            }
            val filteredRegistrations = applyFilter(fromDb.value!!, _selectedFilter.value)
            _uiState.update { it.copy(timeRegistrations = filteredRegistrations) }
        }

        private fun updateFilter(newFilter: FilterType) {
            _selectedFilter.value = newFilter
        }

        fun onFilter(filterType: FilterType) {
            updateFilter(filterType)

            val filteredRegistrations = applyFilter(fromDb.value!!, filterType)
            _uiState.update { it.copy(timeRegistrations = filteredRegistrations) }
        }

        private fun applyFilter(
            timeRegistrations: List<TimeRegistration>,
            filter: FilterType,
        ): ImmutableList<TimeRegistration> {
            val today =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(
                    offset,
                )

            val dayOfWeek = today.dayOfWeek
            val offsetToMonday = if (dayOfWeek == DayOfWeek.SUNDAY) 6 else dayOfWeek.value - 1
            val startOfWeek = today.minus(offsetToMonday.toLong(), DateTimeUnit.DAY)
            val endOfWeek = startOfWeek.plus(6, DateTimeUnit.DAY)

            // Persist filter preferences to user preferences
            viewModelScope.launch {
                userPreferences.setFilterPreferences(
                    FilterPreferences(
                        referenceDate = _referenceDate.value,
                        selectedFilter = _selectedFilter.value,
                        offset = offset,
                    ),
                )
            }

            return when (filter) {
                FilterType.Day -> {
                    timeRegistrations.filter { it.startTime.date == today }
                }
                FilterType.Week -> {
                    timeRegistrations.filter {
                        val date = it.startTime.date
                        date in startOfWeek..endOfWeek
                    }
                }
                FilterType.Month -> {
                    timeRegistrations.filter {
                        val date = it.startTime.date
                        date.month == today.month && date.year == today.year
                    }
                }
            }.toImmutableList()
        }

        fun navigateToPrevious() {
            val difference =
                when (_selectedFilter.value) {
                    FilterType.Day -> DatePeriod(days = -1)
                    FilterType.Week -> DatePeriod(days = -7)
                    FilterType.Month -> DatePeriod(months = -1)
                }

            offset += difference
            _referenceDate.value += difference
            val filteredRegistrations = applyFilter(fromDb.value!!, _selectedFilter.value)
            _uiState.update { it.copy(timeRegistrations = filteredRegistrations) }
        }

        fun navigateToNext() {
            val difference =
                when (_selectedFilter.value) {
                    FilterType.Day -> DatePeriod(days = 1)
                    FilterType.Week -> DatePeriod(days = 7)
                    FilterType.Month -> DatePeriod(months = 1)
                }

            offset += difference
            _referenceDate.value += difference
            val filteredRegistrations = applyFilter(fromDb.value!!, _selectedFilter.value)
            _uiState.update { it.copy(timeRegistrations = filteredRegistrations) }
        }
    }
