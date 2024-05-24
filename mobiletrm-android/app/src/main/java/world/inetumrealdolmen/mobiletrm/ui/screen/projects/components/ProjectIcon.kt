package world.inetumrealdolmen.mobiletrm.ui.screen.projects.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.toJavaLocalDate
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.ui.util.formatTime
import java.time.LocalDate

/**
 * Displays the project's remaining time or deadline as used in [ProjectCard]
 *
 * @param project The project's data
 * @param textColor The color for the text and clock icon.
 * @param modifier Modifier for custom styling.
 */
@Composable
fun ProjectTime(
    project: Project,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_schedule_24),
            contentDescription = stringResource(R.string.contentDescription_deadlineIcon),
            modifier =
                Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
            colorFilter = ColorFilter.tint(textColor),
        )
        Text(
            text =
                when (project.endDate == null) {
                    true ->
                        if (project.workMinutesLeft!! <= 0) {
                            stringResource(R.string.project_finished)
                        } else {
                            formatTime(project.workMinutesLeft)
                        }
                    false -> {
                        if (project.endDate.toJavaLocalDate().isBefore(LocalDate.now())) {
                            stringResource(R.string.project_finished)
                        } else {
                            project.endDate.toString()
                        }
                    }
                },
            color = textColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}
