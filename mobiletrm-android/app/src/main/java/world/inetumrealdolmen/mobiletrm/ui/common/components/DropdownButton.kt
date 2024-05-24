package world.inetumrealdolmen.mobiletrm.ui.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R

/**
 * A dropdown button.
 *
 * @param text The text to display inside the button.
 * @param onClick The function to call whenever the button is pressed.
 * @param modifier Default Compose [Modifier].
 */
@Composable
fun DropdownButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onClick()
            expanded = !expanded
        },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp))
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.contentDescription_toggle_tags),
            modifier = Modifier.size(16.dp),
        )
    }
}
