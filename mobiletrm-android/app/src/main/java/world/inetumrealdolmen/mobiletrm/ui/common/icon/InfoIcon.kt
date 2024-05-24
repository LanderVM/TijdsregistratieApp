package world.inetumrealdolmen.mobiletrm.ui.common.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R

/**
 * An info icon.
 *
 * @param modifier Default Compose [Modifier].
 * @param size The size of the icon in dp. Default is 64.
 *
 */
@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    size: Int = 64,
) {
    Icon(
        imageVector = Icons.Filled.Info,
        contentDescription = stringResource(id = R.string.contentDescription_infoIcon),
        modifier = modifier.size(size.dp),
        tint = MaterialTheme.colorScheme.surfaceTint,
    )
}
