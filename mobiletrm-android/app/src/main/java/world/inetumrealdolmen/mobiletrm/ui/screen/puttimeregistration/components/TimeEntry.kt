package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalTime
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.TimeType
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationResult

/**
 * Section that displays configurable time entries with a button to add more.
 *
 * @param timeEntries the list of time entries to show.
 * @param onAdd the function to call to add a new time entry.
 * @param modifier default Compose [Modifier].
 */
@Composable
fun TimeEntry(
    timeEntries: ImmutableList<TimeEntry>,
    validation: ImmutableList<ValidationResult>,
    onAdd: () -> Unit,
    showAddButton: Boolean,
    onChange: (Long, LocalTime, LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(vertical = 8.dp),
    ) {
        // Display current time entries, with two in each column
        val chunkedEntries = timeEntries.chunked(2)
        chunkedEntries.map { registrationPair ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(registrationPair.size) { i ->
                    TimeField(
                        type = TimeType.START,
                        value = registrationPair[i].startTime,
                        validation = validation.find { it.property == "timeEntries[${registrationPair[i].id}]" },
                        onChange = {
                            onChange(
                                registrationPair[i].id,
                                it,
                                registrationPair[i].endTime,
                            )
                        },
                        modifier = Modifier.weight(0.5f),
                    )
                    TimeField(
                        type = TimeType.END,
                        value = registrationPair[i].endTime,
                        validation = validation.find { it.property == "timeEntries[${registrationPair[i].id}]" },
                        onChange = {
                            onChange(
                                registrationPair[i].id,
                                registrationPair[i].startTime,
                                it,
                            )
                        },
                        modifier = Modifier.weight(0.5f),
                    )
                }
                // Displays an empty element to prevent the final element from taking up an entire row
                if (showAddButton &&
                    timeEntries.size % 2 != 0 &&
                    registrationPair == chunkedEntries.lastOrNull()
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {}
                }
            }
        }

        validation.distinctBy { ValidationResult::type }.forEach {
            Text(
                text =
                    stringResource(
                        id = it.type.message,
                    ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.errorContainer,
                modifier =
                    Modifier.fillMaxWidth()
                        .testTag(stringResource(R.string.testTag_putTimeRegistration_error)),
                textAlign = TextAlign.Center,
            )
        }

        // Button to add new time registration
        if (showAddButton) {
            AddTimeRegistrationButton(
                onClick = onAdd,
                modifier = Modifier.testTag(stringResource(R.string.testTag_putTimeRegistration_timeEntry_addButton)),
            )
        }
    }
}
