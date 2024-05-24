package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.common.components.DialogImpl

/**
 * A dialog to select a time in 24 hour format using [TimePicker].
 *
 * @param onCancel the function to call to dismiss the dialog.
 * @param onConfirm the function to call to update the newly chosen time.
 * @param modifier default Compose [Modifier].
 * @param defaultTime default time to display upon opening the dialog. Defaults to current system time.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    defaultTime: LocalTime =
        LocalTime(
            Clock.System.now().toLocalDateTime(
                TimeZone.currentSystemDefault(),
            ).hour,
            Clock.System.now().toLocalDateTime(
                TimeZone.currentSystemDefault(),
            ).minute,
        ),
) {
    var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }

    val timePickerState: TimePickerState =
        rememberTimePickerState(
            initialHour = defaultTime.hour,
            initialMinute = defaultTime.minute,
        )

    DialogImpl(
        modifier = modifier.testTag(stringResource(R.string.testTag_putTimeRegistration_timeEntry_dialog)),
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.timePickerDialog_title)) },
        buttons = {
            DisplayModeToggleButton(
                displayMode = mode,
                onDisplayModeChange = { mode = it },
            )
            Spacer(Modifier.weight(1f))
            TextButton(onCancel) {
                Text(stringResource(R.string.timePickerDialog_cancel))
            }
            TextButton(
                onClick = { onConfirm(LocalTime(timePickerState.hour, timePickerState.minute)) },
                modifier = Modifier.testTag(stringResource(R.string.testTag_putTimeRegistration_timeEntry_dialog_confirm)),
            ) {
                Text(stringResource(R.string.timePickerDialog_confirm))
            }
        },
    ) {
        val contentModifier = Modifier.padding(horizontal = 24.dp)
        when (mode) {
            DisplayMode.Picker -> TimePicker(modifier = contentModifier, state = timePickerState)
            DisplayMode.Input ->
                TimeInput(
                    modifier = contentModifier.testTag(stringResource(R.string.testTag_putTimeRegistration_timeEntry_dialog_timeField)),
                    state = timePickerState,
                )
        }
    }
}

/**
 * Button to toggle between keyboard mode and GUI mode to select a time.
 *
 * @param displayMode the current mode that is displayed.
 * @param onDisplayModeChange the command to run when the display mode is being changed.
 * @param modifier default Compose [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayModeToggleButton(
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (displayMode) {
        DisplayMode.Picker ->
            IconButton(
                modifier = modifier.testTag(stringResource(R.string.testTag_putTimeRegistration_timeEntry_dialog_keyboardMode)),
                onClick = { onDisplayModeChange(DisplayMode.Input) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.keyboard),
                    contentDescription = stringResource(R.string.contentDescription_showKeyboardMode),
                )
            }

        DisplayMode.Input ->
            IconButton(
                modifier = modifier,
                onClick = { onDisplayModeChange(DisplayMode.Picker) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_schedule_24),
                    contentDescription = stringResource(R.string.contentDescription_showGraphicalMode),
                )
            }
    }
}
