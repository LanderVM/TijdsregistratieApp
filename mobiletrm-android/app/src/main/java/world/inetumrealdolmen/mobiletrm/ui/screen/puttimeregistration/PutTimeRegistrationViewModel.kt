package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import world.inetumrealdolmen.mobiletrm.data.dto.ProjectDTO
import world.inetumrealdolmen.mobiletrm.data.model.ApiCrudState
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.CrudType
import world.inetumrealdolmen.mobiletrm.data.model.PutTimeRegistrationState
import world.inetumrealdolmen.mobiletrm.data.model.Tag
import world.inetumrealdolmen.mobiletrm.data.model.Task
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import world.inetumrealdolmen.mobiletrm.data.repository.impl.PreferencesRepositoryImpl
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.util.now
import world.inetumrealdolmen.mobiletrm.ui.util.parseException
import world.inetumrealdolmen.mobiletrm.ui.util.parsePutException
import world.inetumrealdolmen.mobiletrm.ui.util.toEpochMilli
import world.inetumrealdolmen.mobiletrm.ui.util.toLocalDate
import world.inetumrealdolmen.mobiletrm.ui.validation.Validator
import java.util.UUID

@HiltViewModel(assistedFactory = PutTimeRegistrationViewModel.AddTimeRegistrationViewModelFactory::class)
@OptIn(ExperimentalMaterial3Api::class)
class PutTimeRegistrationViewModel
    @AssistedInject
    constructor(
        @Assisted val localId: UUID?,
        private val repository: QuarkusRepository,
        private val preferences: PreferencesRepositoryImpl,
    ) :
    ViewModel() {
        @AssistedFactory
        interface AddTimeRegistrationViewModelFactory {
            fun create(localId: UUID?): PutTimeRegistrationViewModel
        }

        var fetchDetailsState: ApiState by mutableStateOf(ApiState.Loading)
        var putTimeRegistrationState: ApiCrudState by mutableStateOf(ApiCrudState.Start)

        private val _uiState =
            MutableStateFlow(PutTimeRegistrationState(existingTimeRegistrationLocalId = localId))
        val uiState = _uiState.asStateFlow()

        init {
            fetchDetails(localId)
        }

        fun fetchTimeEntries() {
            viewModelScope.launch {
                try {
                    val response =
                        repository.getTimeEntries(
                            uiState.value.date.selectedDateMillis?.toLocalDate() ?: LocalDate.now(),
                        )
                    _uiState.update { it.copy(existingTimeEntries = response.toImmutableList()) }
                } catch (e: Exception) {
                    fetchDetailsState = parseException(e, "fetchTimeEntries")
                }
                if (uiState.value.date.selectedDateMillis != null && localId == null) {
                    validate()
                }
            }
        }

        private fun fetchDetails(id: UUID?) {
            fetchDetailsState = ApiState.Loading
            viewModelScope.launch {
                fetchDetailsState =
                    try {
                        // Fetch projects with their tasks
                        val projectTasksResponse = repository.getProjectTasks().toImmutableList()
                        _uiState.update { it.copy(projectTasks = projectTasksResponse) }

                        // Existing time registration, get & fill in details
                        if (id != null) {
                            val projectDetailsResponse = repository.getTimeRegistration(id)
                            _uiState.update {
                                it.copy(
                                    existingTimeRegistrationRemoteId = projectDetailsResponse.remoteId,
                                    timeEntries =
                                        persistentListOf(
                                            TimeEntry(
                                                id = projectDetailsResponse.remoteId,
                                                startTime = projectDetailsResponse.startTime.time,
                                                endTime = projectDetailsResponse.endTime.time,
                                            ),
                                        ),
                                    description = projectDetailsResponse.description ?: "",
                                    project = projectDetailsResponse.assignedProject,
                                    task = projectDetailsResponse.assignedTask,
                                    tags = projectDetailsResponse.tags.toPersistentSet(),
                                    date =
                                        DatePickerState(
                                            locale = Locale.current.platformLocale,
                                            initialSelectedDateMillis =
                                                projectDetailsResponse.startTime.date.toEpochMilli(),
                                        ),
                                )
                            }
                        } else {
                            val recentFavorite = repository.getFavoritedProject()
                            if (recentFavorite != null) {
                                if ((recentFavorite.endDate == null && recentFavorite.workMinutesLeft!! > 0) || (
                                        recentFavorite.workMinutesLeft == null &&
                                            recentFavorite.endDate!!.toJavaLocalDate().isAfter(
                                                java.time.LocalDate.now(),
                                            )
                                    )
                                ) {
                                    _uiState.update {
                                        it.copy(
                                            project =
                                                ProjectDTO.IndexShort(
                                                    recentFavorite.id,
                                                    recentFavorite.name,
                                                ),
                                        )
                                    }
                                }
                            }
                        }
                        ApiState.Success
                    } catch (e: Exception) {
                        parseException(e, "fetchDetails")
                    }
            }
        }

        private fun validate(): Boolean {
            val validateState = Validator(PutTimeRegistrationState::class)
            val errors = validateState.validate(_uiState.value, _uiState.value.existingTimeEntries)
            _uiState.update {
                it.copy(
                    validationResults = errors,
                    hasChanged = true,
                )
            }
            return errors.isNotEmpty()
        }

        fun putTimeRegistrations() {
            putTimeRegistrationState = ApiCrudState.Loading

            if (validate()) {
                putTimeRegistrationState = ApiCrudState.Start
                return
            }

            viewModelScope.launch {
                putTimeRegistrationState =
                    try {
                        ApiCrudState.Loading
                        val date: LocalDate =
                            _uiState.value.date.selectedDateMillis?.toLocalDate() ?: LocalDate.now()

                        if (localId == null) {
                            // New time registration
                            val registrations =
                                _uiState.value.timeEntries
                                    .filter {
                                        !(
                                            it.startTime == LocalTime(0, 0) &&
                                                it.endTime == LocalTime(0, 0)
                                        )
                                    }
                                    .map {
                                        TimeRegistration(
                                            description = _uiState.value.description.trim(),
                                            startTime = LocalDateTime(date, it.startTime),
                                            endTime = LocalDateTime(date, it.endTime),
                                            assignedProject = _uiState.value.project,
                                            assignedTask = _uiState.value.task,
                                            tags = _uiState.value.tags.toList(),
                                        )
                                    }
                            repository.createTimeRegistration(registrations)
                            preferences.setSnackbarPreferences(preferences.getSnackbarPreferences().copy(timeRegistrationCreated = true))
                            ApiCrudState.Success(CrudType.Create)
                        } else {
                            // Update existing registration
                            val new =
                                TimeRegistration(
                                    remoteId = _uiState.value.existingTimeRegistrationRemoteId,
                                    localId = _uiState.value.existingTimeRegistrationLocalId!!,
                                    description = _uiState.value.description.trim(),
                                    startTime = LocalDateTime(date, _uiState.value.timeEntries[0].startTime),
                                    endTime = LocalDateTime(date, _uiState.value.timeEntries[0].endTime),
                                    assignedProject = _uiState.value.project,
                                    assignedTask = _uiState.value.task,
                                    tags = _uiState.value.tags.toList(),
                                )
                            repository.updateTimeRegistration(new)
                            preferences.setSnackbarPreferences(preferences.getSnackbarPreferences().copy(timeRegistrationUpdated = true))
                            ApiCrudState.Success(CrudType.Update)
                        }
                    } catch (e: Exception) {
                        parsePutException(e, "putTimeRegistrations")
                    }
            }
        }

        fun addTag(newTag: String) {
            val tag = Tag(name = newTag.trim())
            if (tag.name.isNotEmpty() && !_uiState.value.tags.contains(tag)) {
                _uiState.update { it.copy(tags = (it.tags + tag).toPersistentSet()) }
            }
            validate()
        }

        fun addNewTimeEntry() {
            if (_uiState.value.timeEntries.size < 4) {
                _uiState.update {
                    val nextId = it.timeEntries.maxOfOrNull(TimeEntry::id)!!.plus(1)
                    it.copy(
                        timeEntries = (it.timeEntries + TimeEntry(id = nextId)).toPersistentList(),
                    )
                }
            }
            validate()
        }

        fun setDescription(description: String) {
            _uiState.update { it.copy(description = description) }
            validate()
        }

        fun setTask(task: Task?) {
            _uiState.update { it.copy(task = task) }
            validate()
        }

        fun setProject(
            projectId: Long,
            projectName: String,
        ) {
            _uiState.update {
                it.copy(
                    project = if (projectId == -1L) null else ProjectDTO.IndexShort(projectId, projectName),
                    task = null,
                )
            }
            validate()
        }

        fun updateTimeEntry(
            id: Long,
            startTime: LocalTime,
            endTime: LocalTime,
        ) {
            _uiState.update {
                it.copy(
                    timeEntries =
                        it.timeEntries.map { entry ->
                            if (entry.id == id) {
                                entry.copy(startTime = startTime, endTime = endTime)
                            } else {
                                entry
                            }
                        }.toPersistentList(),
                )
            }
            validate()
        }

        fun deleteRegistration(localId: UUID) {
            putTimeRegistrationState = ApiCrudState.Loading

            viewModelScope.launch {
                putTimeRegistrationState =
                    try {
                        repository.deleteTimeRegistrationById(localId)
                        preferences.setSnackbarPreferences(preferences.getSnackbarPreferences().copy(timeRegistrationDeleted = true))
                        ApiCrudState.Success(CrudType.Delete)
                    } catch (e: Exception) {
                        parsePutException(e, "deleteRegistration")
                    }
            }
        }
    }
