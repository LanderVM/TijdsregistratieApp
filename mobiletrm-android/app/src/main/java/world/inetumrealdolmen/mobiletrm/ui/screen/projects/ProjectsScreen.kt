package world.inetumrealdolmen.mobiletrm.ui.screen.projects

import ProjectsList
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.collections.immutable.toImmutableList
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavScaffold
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes

@Composable
fun ProjectsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProjectsViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
) {
    val userState by viewModel.userState.collectAsState()

    NavScaffold(
        navController,
        title =
            stringResource(
                id = R.string.nav_title_projects_overview,
                userState.givenName ?: stringResource(R.string.nav_title_unknown_username),
            ),
        modifier = modifier,
        userInfo = userState,
    ) { padding ->

        when (val state = viewModel.viewModelState) {
            is ApiState.Error ->
                Indicator(
                    type = IndicatorType.ERROR,
                    error = state.details,
                )

            ApiState.Loading -> Indicator(IndicatorType.LOADING)

            is ApiState.Success -> {
                val projects by viewModel.projects.collectAsState()
                ProjectsList(
                    projects =
                        projects.toImmutableList(),
                    onNavigate = { navController.navigate("${NavigationRoutes.ProjectDetailsOverview.name}/$it") },
                    onFavorite = { id, favorite -> favorite(id, favorite, context, viewModel) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

private fun favorite(
    id: Long,
    favorite: Boolean,
    context: Context,
    viewModel: ProjectsViewModel,
) {
    viewModel.setFavorite(id, favorite)
    Toast.makeText(
        context,
        context.getString(
            if (favorite) {
                R.string.project_favorited
            } else {
                R.string.project_unfavorited
            },
        ),
        Toast.LENGTH_SHORT,
    )
        .show()
}
