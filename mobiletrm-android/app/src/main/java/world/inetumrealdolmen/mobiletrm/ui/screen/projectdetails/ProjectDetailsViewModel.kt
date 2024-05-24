package world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetails
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetailsState
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.util.parseException
import javax.inject.Inject

@HiltViewModel
class ProjectDetailsViewModel
    @Inject
    constructor(private val repository: QuarkusRepository) :
    ViewModel() {
        var apiState: ApiState by mutableStateOf(ApiState.Loading)

        private val _uiState = MutableStateFlow(ProjectDetailsState(ProjectDetails()))
        val uiState = _uiState.asStateFlow()

        fun fetchProjectDetails(id: Long) {
            apiState = ApiState.Loading
            viewModelScope.launch {
                apiState =
                    try {
                        val response = repository.getProjectDetails(id)
                        _uiState.update { it.copy(details = response) }
                        ApiState.Success
                    } catch (e: Exception) {
                        parseException(e, "fetchProjectDetails")
                    }
            }
        }
    }
