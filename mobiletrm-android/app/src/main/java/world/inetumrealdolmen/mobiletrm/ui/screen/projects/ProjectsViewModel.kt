package world.inetumrealdolmen.mobiletrm.ui.screen.projects

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.result.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.data.model.isCompleted
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.util.parseException
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel
    @Inject
    constructor(
        private val repository: QuarkusRepository,
        userInfo: UserProfile,
    ) :
    ViewModel() {
        var viewModelState: ApiState by mutableStateOf(ApiState.Loading)

        val userState = MutableStateFlow(userInfo).asStateFlow()

        private lateinit var fromDb: StateFlow<List<Project>?>
        val projects = MutableStateFlow(listOf<Project>())

        init {
            fetchProject()
        }

        private fun fetchProject() {
            viewModelState = ApiState.Loading
            viewModelScope.launch {
                try {
                    // Fetch projects
                    fromDb =
                        repository.getProjects().stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5_000L),
                            initialValue = null,
                        )

                    // Prevent showing success before items have been fetched from API
                    fromDb.drop(1).collect {
                        applyFilter()
                        viewModelState = ApiState.Success
                    }
                } catch (e: Exception) {
                    viewModelState = parseException(e, "fetchProject")
                }
            }
        }

        private fun applyFilter() {
            projects.update {
                fromDb.value!!.sortedWith(
                    compareByDescending<Project> { it.isFavorite }
                        .thenBy { it.isCompleted() },
                )
            }
        }

        fun setFavorite(
            id: Long,
            favorite: Boolean,
        ) {
            viewModelScope.launch {
                try {
                    repository.favoriteProject(id, favorite)
                } catch (e: Exception) {
                    parseException(e, "setFavorite")
                }
            }
        }
    }
