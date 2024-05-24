package world.inetumrealdolmen.mobiletrm.ui.common.navigation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable function that represents an individual item within the navigation drawer.
 *
 * @param title The title text for the item.
 * @param modifier Modifier for custom styling.
 * @param onClick Callback for item clicks.
 */
@Composable
fun NavItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        colors =
            NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Transparent,
            ),
        label = {
            Box(
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = title.uppercase(),
                    maxLines = 2,
                    fontSize = 30.sp,
                    lineHeight = 35.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        selected = false,
        onClick = { onClick() },
        modifier =
            modifier
                .height(80.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding),
    )
}
