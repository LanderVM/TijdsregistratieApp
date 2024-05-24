package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.screen.registrationtimer.TimerMode

@Composable
fun ModeSwitch(
    timerMode: TimerMode,
    changeTimerMode: (TimerMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val startShape =
        RoundedCornerShape(
            topStartPercent = 25,
            bottomStartPercent = 25,
            topEndPercent = 0,
            bottomEndPercent = 0,
        )
    val endShape =
        RoundedCornerShape(
            topStartPercent = 0,
            bottomStartPercent = 0,
            topEndPercent = 25,
            bottomEndPercent = 25,
        )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
    ) {
        TextButton(
            onClick = {
                if (timerMode == TimerMode.Manual) {
                    changeTimerMode(TimerMode.Timer)
                }
            },
            shape = startShape,
            colors =
                ButtonDefaults.textButtonColors(
                    containerColor =
                        if (timerMode == TimerMode.Timer) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                    contentColor =
                        if (timerMode == TimerMode.Timer) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                ),
            modifier =
                Modifier
                    .weight(1f)
                    .testTag(stringResource(R.string.testTag_putTimeRegistration_modeSwitch_timer)),
        ) {
            Text(
                text = stringResource(R.string.timeRegistration_switch_timer),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
        TextButton(
            onClick = {
                if (timerMode == TimerMode.Timer) {
                    changeTimerMode(TimerMode.Manual)
                }
            },
            shape = endShape,
            colors =
                ButtonDefaults.textButtonColors(
                    containerColor =
                        if (timerMode == TimerMode.Manual) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                    contentColor =
                        if (timerMode == TimerMode.Manual) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                ),
            modifier =
                Modifier
                    .weight(1f)
                    .testTag(stringResource(R.string.testTag_putTimeRegistration_modeSwitch_manual)),
        ) {
            Text(
                text = stringResource(R.string.timeRegistration_switch_manual),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}
