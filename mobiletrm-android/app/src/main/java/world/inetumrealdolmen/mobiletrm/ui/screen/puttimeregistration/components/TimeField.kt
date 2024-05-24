package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalTime
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.common.components.Subtitle
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.TimeType
import world.inetumrealdolmen.mobiletrm.ui.util.formatTime
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationResult

/**
 * Displays a user-modifiable time using a [TimePickerDialog].
 *
 * @param type the type of TimeField
 * @param value the time value to display.
 * @param onChange the function to call whenever the user changes the time.
 * @param modifier default Compose [Modifier].
 */
@Composable
fun TimeField(
    type: TimeType,
    value: LocalTime,
    validation: ValidationResult?,
    onChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Subtitle(
            text =
                when (type) {
                    TimeType.START -> R.string.timeRegistrations_subTitle_startHour
                    TimeType.END -> R.string.timeRegistrations_subTitle_endHour
                },
        )

        OutlinedTextField(
            value = formatTime(value, multiLine = true),
            enabled = false,
            readOnly = true,
            onValueChange = {},
            modifier =
                Modifier
                    .width(IntrinsicSize.Max)
                    .clickable {
                        showTimePicker = true
                    }
                    .testTag(stringResource(R.string.testTag_putTimeRegistration_timeEntry)),
            colors =
                OutlinedTextFieldDefaults.colors().copy(
                    disabledSuffixColor = MaterialTheme.colorScheme.background,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor =
                        if (validation != null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.background,
                ),
            textStyle =
                LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                ),
            isError = validation != null,
        )

        if (showTimePicker) {
            TimePickerDialog(
                defaultTime = value,
                onCancel = { showTimePicker = false },
                onConfirm = {
                    showTimePicker = false
                    onChange(it)
                },
            )
        }
    }
}
