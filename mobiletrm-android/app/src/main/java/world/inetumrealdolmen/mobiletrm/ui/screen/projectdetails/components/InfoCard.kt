package world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import world.inetumrealdolmen.mobiletrm.ui.common.components.DropdownButton
import world.inetumrealdolmen.mobiletrm.ui.common.components.Tag
import world.inetumrealdolmen.mobiletrm.ui.util.formatTime
import java.util.UUID

@Composable
fun InfoCard(
    timeRegistration: TimeRegistration,
    canDelete: Boolean,
    onDelete: (UUID) -> Unit,
    onEdit: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    canModify: Boolean = false,
) {
    var visible by remember { mutableStateOf(false) }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag(stringResource(R.string.testTag_timeRegistrationOverview_infoCard_card)),
        elevation = CardDefaults.cardElevation(2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (timeRegistration.remoteId == -1L) {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                contentColor =
                    if (timeRegistration.remoteId == -1L) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
            ),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 8.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${formatTime(timeRegistration.startTime.time)} - ${formatTime(timeRegistration.endTime.time)}",
                        modifier = Modifier.testTag(stringResource(R.string.testTag_timeRegistrationOverview_infoCard_time)),
                    )
                    Text(
                        when {
                            timeRegistration.assignedProject != null && timeRegistration.assignedTask != null ->
                                "${timeRegistration.assignedProject.name} - ${timeRegistration.assignedTask.taskName}"

                            timeRegistration.assignedProject != null -> timeRegistration.assignedProject.name
                            timeRegistration.assignedTask != null -> timeRegistration.assignedTask.taskName
                            else -> "No project selected"
                        },
                        color =
                            if (timeRegistration.remoteId == -1L) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6F)
                            },
                        modifier = Modifier.testTag(stringResource(R.string.testTag_timeRegistrationOverview_infoCard_info)),
                    )
                }
                when {
                    canModify && timeRegistration.tags.isEmpty() -> {
                        when (timeRegistration.syncingError) {
                            null ->
                                if (timeRegistration.assignedProject != null && timeRegistration.remoteId == -1L) {
                                    Icon(
                                        painter = painterResource(R.drawable.cloud_sync),
                                        contentDescription = stringResource(R.string.contentDescription_timeRegistrationStatus_syncIcon),
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            else ->
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = stringResource(R.string.contentDescription_timeRegistrationStatus_warningIcon),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                        }
                        if (canDelete) {
                            ModifyMenu(
                                timeRegistration.localId,
                                onDelete,
                                onEdit,
                            )
                        }
                    }
                    !canModify && timeRegistration.tags.isNotEmpty() ->
                        DropdownButton(
                            text = stringResource(R.string.projectDetails_show_tags),
                            onClick = { visible = !visible },
                        )
                }
            }
        }
        if (canModify && timeRegistration.tags.isNotEmpty()) {
            Column {
                InfoCardOptions(
                    timeRegistration = timeRegistration,
                    canDelete = canDelete,
                    onClick = { visible = !visible },
                    onDelete = onDelete,
                    onEdit = onEdit,
                )
            }
        }
        AnimatedVisibility(visible) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                timeRegistration.tags.forEach { tag ->
                    Tag(tag.name)
                }
            }
        }
    }
}

@Composable
fun InfoCardOptions(
    timeRegistration: TimeRegistration,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDelete: (UUID) -> Unit,
    onEdit: (UUID) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(bottom = 8.dp, top = 4.dp, start = 16.dp, end = 16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            if (timeRegistration.tags.isNotEmpty()) {
                DropdownButton(
                    text = stringResource(R.string.projectDetails_show_tags),
                    onClick = onClick,
                )
            }
        }
        when (timeRegistration.syncingError) {
            null ->
                if (timeRegistration.assignedProject != null && timeRegistration.remoteId == -1L) {
                    Icon(
                        painter = painterResource(R.drawable.cloud_sync),
                        contentDescription = stringResource(R.string.contentDescription_timeRegistrationStatus_syncIcon),
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            else ->
                Icon(
                    imageVector = Icons.Default.Warning,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = stringResource(R.string.contentDescription_timeRegistrationStatus_warningIcon),
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
        }
        if (canDelete) {
            ModifyMenu(
                timeRegistration.localId,
                onDelete,
                onEdit,
            )
        }
    }
}

@Composable
fun ModifyMenu(
    timeRegistrationLocalId: UUID,
    onDelete: (UUID) -> Unit,
    onEdit: (UUID) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .wrapContentSize(Alignment.TopStart),
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.testTag(stringResource(R.string.testTag_timeRegistrationOverview_infoCard_moreOptions)),
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.contentDescription_moreVertIcon),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 8.dp,
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .width(IntrinsicSize.Max)
                    .padding(horizontal = 12.dp),
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.projectDetails_edit)) },
                onClick = {
                    onEdit(timeRegistrationLocalId)
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.contentDescription_editIcon),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                },
                modifier = Modifier.testTag(stringResource(R.string.testTag_timeRegistrationOverview_infoCard_edit)),
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                thickness = 1.dp,
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.projectDetails_delete)) },
                onClick = {
                    onDelete(timeRegistrationLocalId)
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.contentDescription_deleteIcon),
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
                modifier = Modifier.testTag(stringResource(R.string.testTag_timeRegistrationOverview_infoCard_delete)),
            )
        }
    }
}
