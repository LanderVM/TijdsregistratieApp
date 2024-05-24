package world.inetumrealdolmen.mobiletrm.ui.common.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R

/**
 * An Error icon.
 *
 * @param modifier Default Compose [Modifier].
 * @param size The size of the icon in dp. Default is 64.
 *
 */
@Composable
fun ErrorIcon(
    modifier: Modifier = Modifier,
    size: Int = 64,
) {
    Icon(
        imageVector = Icons.Filled.Warning,
        contentDescription = stringResource(id = R.string.contentDescription_errorIcon),
        modifier = modifier.size(size.dp),
        tint = MaterialTheme.colorScheme.error,
    )
}
