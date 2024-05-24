package world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.util.formatDate
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectTimeRegistrations(
    times: ImmutableList<TimeRegistration>,
    runningProjects: ImmutableList<Long>,
    modifier: Modifier = Modifier,
    onEdit: (UUID) -> Unit = {},
    onDelete: (UUID) -> Unit = {},
    canModify: Boolean = false,
) {
    val timeRegistrationsPerDay = times.groupBy { it.startTime.date }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (times.isEmpty()) {
            true ->
                Indicator(
                    type = IndicatorType.INFO,
                    text = stringResource(R.string.timeRegistration_notFound),
                )
            false ->
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    timeRegistrationsPerDay.keys.sortedDescending().forEach { day ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillParentMaxWidth(),
                            ) {
                                Text(
                                    text = formatDate(day),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier =
                                        Modifier
                                            .padding(vertical = 12.dp)
                                            .padding(start = 16.dp),
                                )
                            }
                        }
                        items(timeRegistrationsPerDay[day] ?: emptyList()) { timeRegistration ->
                            InfoCard(
                                timeRegistration = timeRegistration,
                                modifier =
                                    Modifier.testTag(stringResource(R.string.testTag_timeRegistrationOverview_infoCard)),
                                canModify = canModify,
                                onDelete = onDelete,
                                onEdit = onEdit,
                                canDelete =
                                    timeRegistration.assignedProject == null ||
                                        runningProjects.contains(timeRegistration.assignedProject.id),
                            )
                        }
                    }
                }
        }
    }
}
