package world.inetumrealdolmen.mobiletrm.ui.common.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * A subtitle
 *
 * @param modifier Default Compose [Modifier].
 * @param text The optional text to display if there is no [content].
 * @param content The optional content to display if there is no [text].
 */
@Composable
fun Subtitle(
    modifier: Modifier = Modifier,
    @StringRes text: Int = Int.MIN_VALUE,
    content: @Composable () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (text != Int.MIN_VALUE) {
            Text(
                stringResource(text),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            content()
        }
    }
}
