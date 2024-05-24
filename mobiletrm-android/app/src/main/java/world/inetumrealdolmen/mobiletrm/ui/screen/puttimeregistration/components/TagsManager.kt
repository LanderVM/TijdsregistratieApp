package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentSet
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.Tag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsManager(
    tags: PersistentSet<Tag>,
    onTagAdded: (String) -> Unit,
    showButton: Boolean,
    modifier: Modifier = Modifier,
) {
    var showTagField by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        TagsHeader({ showTagField = it }, showButton)
        if (showTagField) {
            var newTag by rememberSaveable { mutableStateOf("") }

            OutlinedTextField(
                value = newTag,
                onValueChange = { newTag = it },
                singleLine = true,
                placeholder = { Text(text = stringResource(R.string.timeRegistrations_tags_PlaceHolder)) },
                keyboardActions =
                    KeyboardActions(onNext = {
                        if (newTag.isNotBlank()) {
                            onTagAdded(newTag)
                            newTag = ""
                            showTagField = false
                        }
                    }),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, autoCorrectEnabled = true),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            tags.forEach { tag ->
                Chip(label = tag.name)
            }
        }
    }
}

@Composable
fun TagsHeader(
    onShowTagFieldChange: (Boolean) -> Unit,
    showButton: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(0.8f)
                    .clip(
                        RoundedCornerShape(
                            8.dp,
                        ),
                    )
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.timeRegistrations_tags),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (showButton) {
            Button(
                onClick = { onShowTagFieldChange(true) },
                shape = RoundedCornerShape(8.dp),
                colors =
                    ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray,
                    ),
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.timeRegistrations_tags_addIcon),
                )
            }
        }
    }
}

@Composable
fun Chip(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onBackground)
    }
}
