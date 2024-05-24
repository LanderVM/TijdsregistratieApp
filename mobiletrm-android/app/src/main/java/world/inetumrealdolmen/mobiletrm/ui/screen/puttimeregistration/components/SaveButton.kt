package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import world.inetumrealdolmen.mobiletrm.R

/**
 * Button used to save a new time registration.
 *
 * @param onClick the viewmodel function to call to save the time registration.
 * @param modifier default Compose [Modifier].
 */
@Composable
fun SaveButton(
    onClick: () -> Unit,
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isDisabled: Boolean = false,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .testTag(stringResource(R.string.testTag_putTimeRegistration_save)),
        shape = RoundedCornerShape(15),
        colors =
            ButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        enabled = !isLoading && !isDisabled,
    ) {
        Text(
            text = stringResource(text),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
        )
    }
}
