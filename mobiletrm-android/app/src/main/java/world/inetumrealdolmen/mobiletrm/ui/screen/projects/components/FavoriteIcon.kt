package world.inetumrealdolmen.mobiletrm.ui.screen.projects.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import world.inetumrealdolmen.mobiletrm.R

@Composable
fun FavoriteIcon(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        modifier = modifier,
        checked = checked,
        onCheckedChange = { onCheckedChange(it) },
    ) {
        Icon(
            imageVector = if (checked) Icons.Filled.Star else Icons.TwoTone.Star,
            contentDescription = stringResource(R.string.contentDescription_favouriteIcon),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
