package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateManager(
    datePickerState: DatePickerState,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val newDateState =
        remember {
            DatePickerState(
                Locale.current.platformLocale,
                initialSelectedDateMillis = datePickerState.selectedDateMillis,
            )
        }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis = newDateState.selectedDateMillis
                }) {
                    Text(stringResource(R.string.datePickerDialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.datePickerDialog_cancel))
                }
            },
        ) {
            DatePicker(state = newDateState)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(0.5f)
                    .clip(
                        RoundedCornerShape(
                            8.dp,
                        ),
                    )
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.datePickerDialog_title),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        OutlinedButton(
            onClick = { showDatePicker = true },
            shape = RoundedCornerShape(8.dp),
            modifier =
                Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp),
        ) {
            Text(
                text =
                    datePickerState.selectedDateMillis?.let {
                        formatDate(
                            Instant.fromEpochMilliseconds(it),
                            pattern = "dd/MM/yy",
                        )
                    } ?: run {
                        formatDate(
                            Clock.System.now(),
                            pattern = "dd/MM/yy",
                        )
                    },
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.datePickerDialog_title),
            )
        }
    }
}
