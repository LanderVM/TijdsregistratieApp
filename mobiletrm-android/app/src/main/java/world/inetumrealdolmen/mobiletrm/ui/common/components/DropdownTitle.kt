package world.inetumrealdolmen.mobiletrm.ui.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import world.inetumrealdolmen.mobiletrm.R

/**
 * A clickable title.
 *
 * @param onClick The function to call whenever the title is pressed.
 * @param modifier Default Compose [Modifier].
 */
@Composable
fun DropdownTitle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isOpen by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text =
                stringResource(
                    if (isOpen) {
                        R.string.projectDetails_hide_project_information
                    } else {
                        R.string.projectDetails_show_project_information
                    },
                ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = {
            onClick()
            isOpen = !isOpen
        }) {
            Icon(
                imageVector = if (isOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.contentDescription_toggle_project_info),
            )
        }
    }
}
