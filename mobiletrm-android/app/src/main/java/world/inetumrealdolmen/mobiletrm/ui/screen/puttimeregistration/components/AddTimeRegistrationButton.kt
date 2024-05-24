package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.common.components.Subtitle

/**
 * Button to add a new Time Registration
 *
 * @param onClick the function to call when the button is pressed.
 * @param modifier default Compose [Modifier].
 */
@Composable
fun AddTimeRegistrationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Subtitle(
        modifier =
            modifier
                .padding(vertical = 4.dp)
                .clickable {
                    onClick()
                },
    ) {
        Icon(
            Icons.Default.Add,
            stringResource(R.string.timeRegistrations_addRegistrationButton),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
