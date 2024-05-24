import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.screen.projects.components.ProjectCard

@Composable
fun ProjectsList(
    projects: ImmutableList<Project>,
    onNavigate: (Long) -> Unit,
    onFavorite: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (projects.isEmpty()) {
        Indicator(
            IndicatorType.INFO,
            text = stringResource(id = R.string.infoIndicator_noProjects),
        )
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(
                    projects,
                ) { index, project ->
                    ProjectCard(
                        index,
                        project,
                        onNavigate,
                        { id, favorite -> onFavorite(id, favorite) },
                        modifier = Modifier.testTag(stringResource(R.string.testTag_projectCard)),
                    )
                }
            }
        }
    }
}
