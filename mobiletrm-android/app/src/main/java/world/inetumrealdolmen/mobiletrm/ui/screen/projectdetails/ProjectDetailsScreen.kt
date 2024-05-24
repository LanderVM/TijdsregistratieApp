package world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.ui.common.components.DropdownTitle
import world.inetumrealdolmen.mobiletrm.ui.common.components.Subtitle
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavScaffold
import world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components.ProjectDescription
import world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components.ProjectInfo
import world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components.ProjectTimeRegistrations

@Composable
fun ProjectDetailsScreen(
    navController: NavHostController,
    projectId: Long,
    modifier: Modifier = Modifier,
    viewModel: ProjectDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProjectDetails(projectId)
    }

    NavScaffold(
        navController,
        modifier = modifier,
        title = uiState.details.name.ifBlank { stringResource(R.string.nav_title_project_details_overview_unknown) },
    ) { padding ->

        var showProjectInfo by remember { mutableStateOf(false) }

        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
        ) {
            DropdownTitle(
                onClick = { showProjectInfo = !showProjectInfo },
            )
            if (showProjectInfo && viewModel.apiState == ApiState.Success) {
                when (uiState.details.customer.id == -1L) {
                    true ->
                        Indicator(
                            type = IndicatorType.INFO,
                            text = stringResource(R.string.projectDetails_noCachedDetails),
                            useEntireScreen = false,
                            isTextOnly = true,
                        )
                    false -> {
                        ProjectInfo(uiState.details)
                        ProjectDescription(description = uiState.details.description)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Subtitle(text = R.string.projectDetails_time_registrations)

            when (val state = viewModel.apiState) {
                ApiState.Loading -> Indicator(IndicatorType.LOADING)
                is ApiState.Error ->
                    Indicator(
                        type = IndicatorType.ERROR,
                        error = state.details,
                    )
                is ApiState.Success -> ProjectTimeRegistrations(uiState.details.timeRegistration.toImmutableList(), persistentListOf())
            }
        }
    }
}
