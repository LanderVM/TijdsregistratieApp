package world.inetumrealdolmen.mobiletrm.ui.common.icon

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A loading icon.
 *
 * @param modifier Modifier for custom styling.
 */
@Composable
fun LoadingIcon(
    modifier: Modifier = Modifier,
    size: Int = 64,
) {
    CircularProgressIndicator(
        modifier =
            modifier
                .width(size.dp)
                .height(size.dp),
    )
}
