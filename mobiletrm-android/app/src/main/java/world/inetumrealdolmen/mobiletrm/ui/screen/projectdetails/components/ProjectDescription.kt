package world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R

@Composable
fun ProjectDescription(
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            stringResource(R.string.projectDetails_description),
            fontWeight = FontWeight.Bold,
        )
        Text(description)
    }
}
