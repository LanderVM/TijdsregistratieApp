package world.inetumrealdolmen.mobiletrm.ui.screen.projects.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.data.model.isCompleted

/**
 * Displays a simple project overview using [Card]
 *
 * @param index The index of the project item used to determine which background colour to use.
 * @param project The project's data to display.
 * @param onNavigate The function to call when a user wants to navigate to the project's details page.
 * @param onFavorite The function to call when a user wants to (un)favorite the project.
 * @param modifier Modifier for custom styling.
 */
@Composable
fun ProjectCard(
    index: Int,
    project: Project,
    onNavigate: (Long) -> Unit,
    onFavorite: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .padding(vertical = 4.dp)
                .clickable {
                    onNavigate(project.id)
                }.testTag("Card"),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = if (index % 2 == 0) 1F else 0.75F))
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = project.name,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                )
                ProjectTime(project, MaterialTheme.colorScheme.onPrimary)
            }
            if (!project.isCompleted()) {
                FavoriteIcon(project.isFavorite, { onFavorite(project.id, it) })
            }
        }
    }
}
