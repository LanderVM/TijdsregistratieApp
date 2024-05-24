package world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaLocalDate
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetails
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.util.formatTime
import java.time.LocalDate

@Composable
fun ProjectInfo(
    details: ProjectDetails?,
    modifier: Modifier = Modifier,
) {
    when (details) {
        null -> Indicator(type = IndicatorType.INFO, text = stringResource(R.string.projectDetails_noCachedDetails))
        else ->
            Card(
                elevation = CardDefaults.cardElevation(4.dp),
                modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(stringResource(R.string.projectDetails_client), fontWeight = FontWeight.Bold)
                    Text(details.customer.name)

                    HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline)

                    Text(stringResource(R.string.projectDetails_company), fontWeight = FontWeight.Bold)
                    Text(details.customer.companyName)

                    HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline)

                    Text(stringResource(R.string.projectDetails_startDate), fontWeight = FontWeight.Bold)
                    Text(details.startDate.toString())

                    HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline)

                    Text(
                        stringResource(
                            if (details.endDate == null) {
                                R.string.projectDetails_timeRemaining
                            } else {
                                R.string.projectDetails_deadline
                            },
                        ),
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        when (details.endDate == null) {
                            true ->
                                if (details.workMinutesLeft!! <= 0) {
                                    stringResource(R.string.project_finished)
                                } else {
                                    formatTime(details.workMinutesLeft)
                                }
                            false -> {
                                if (details.endDate.toJavaLocalDate().isBefore(LocalDate.now())) {
                                    stringResource(R.string.project_finished)
                                } else {
                                    details.endDate.toString()
                                }
                            }
                        },
                    )
                }
            }
    }
}
