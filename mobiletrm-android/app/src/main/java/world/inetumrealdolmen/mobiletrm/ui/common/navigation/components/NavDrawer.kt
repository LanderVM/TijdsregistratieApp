package world.inetumrealdolmen.mobiletrm.ui.common.navigation.components

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes

/**
 * Composable function that represents a navigation drawer with a list of items.
 *
 * @param context The Android context.
 * @param modifier Modifier for custom styling.
 * @param onClick Callback for item clicks.
 */
@Composable
fun NavDrawer(
    context: Context,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    ModalDrawerSheet(
        modifier = modifier.alpha(0.9f),
    ) {
        Spacer(
            modifier = Modifier.height(16.dp),
        )
        NavigationRoutes.entries.forEach { item ->
            if (item.showInDrawer) {
                NavItem(
                    modifier =
                        Modifier.testTag(
                            stringResource(
                                id = R.string.testTag_navigateHamburger_button,
                                item.name,
                            ),
                        ),
                    title = item.getString(context, true),
                    onClick = { onClick(item.name) },
                )
            }
        }
    }
}
