package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.data.model.ProjectTasks
import world.inetumrealdolmen.mobiletrm.data.model.Task
import world.inetumrealdolmen.mobiletrm.ui.validation.ValidationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDropdownMenu(
    projectTasks: ImmutableList<ProjectTasks>,
    selectedProject: Project?,
    selectedTask: Task?,
    validation: ValidationResult?,
    onProjectSelected: (Long, String) -> Unit,
    isEdit: Boolean,
    onTaskSelected: (Task?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedProject by remember { mutableStateOf(false) }
    var expandedTask by remember { mutableStateOf(false) }

    var selectedTaskName by remember { mutableStateOf("") }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.timeRegistrations_project),
            modifier = Modifier.padding(bottom = 4.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        ExposedDropdownMenuBox(
            expanded = expandedProject,
            onExpandedChange = {
                expandedProject = !expandedProject
            },
            modifier = Modifier.testTag(stringResource(R.string.testTag_putTimeRegistration_projectDropdown)),
        ) {
            OutlinedTextField(
                value = selectedProject?.name ?: "",
                onValueChange = { },
                placeholder = { Text(text = stringResource(R.string.timeRegistrations_project_placeHolder)) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.projecticon),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(32.dp)
                                .padding(end = 4.dp),
                    )
                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProject) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .padding(bottom = 8.dp)
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .testTag(stringResource(R.string.testTag_putTimeRegistration_projectDropdown_value)),
                isError = validation != null,
            )

            ExposedDropdownMenu(
                expanded = expandedProject,
                onDismissRequest = { expandedProject = false },
            ) {
                (
                    if (!isEdit) {
                        mutableListOf(
                            ProjectTasks(name = stringResource(id = R.string.timeRegistrations_task_none)),
                        ) + projectTasks
                    } else {
                        projectTasks
                    }
                ).forEach {
                        project ->
                    DropdownMenuItem(
                        text = { Text(text = project.name) },
                        onClick = {
                            onProjectSelected(project.id, project.name)
                            onTaskSelected(null)
                            expandedProject = false
                        },
                        modifier =
                            Modifier.testTag(
                                stringResource(R.string.testTag_putTimeRegistration_projectDropdown_option, project.name),
                            ),
                    )
                }
            }
        }
        if (validation != null) {
            Text(
                text = stringResource(id = validation.type.message),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onError,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp)
                        .testTag(stringResource(R.string.testTag_putTimeRegistration_error)),
                textAlign = TextAlign.Start,
            )
        }

        Text(
            text = stringResource(R.string.timeRegistrations_task),
            modifier = Modifier.padding(bottom = 4.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        ExposedDropdownMenuBox(
            expanded = expandedTask,
            onExpandedChange = {
                if (selectedProject != null) {
                    expandedTask = !expandedTask
                }
            },
            modifier = Modifier.testTag(stringResource(R.string.testTag_putTimeRegistration_taskDropdown)),
        ) {
            OutlinedTextField(
                value = selectedTask?.taskName ?: "",
                onValueChange = { },
                placeholder = { Text(text = stringResource(R.string.timeRegistrations_task_placeHolder)) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.taskicon),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(32.dp)
                                .padding(end = 4.dp),
                    )
                },
                readOnly = true,
                enabled = selectedProject != null,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTask) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .padding(bottom = 8.dp)
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .testTag(stringResource(R.string.testTag_putTimeRegistration_taskDropdown_value)),
            )

            val tasks: MutableList<Task> =
                mutableListOf(Task(taskName = stringResource(id = R.string.timeRegistrations_task_none)))
            tasks += projectTasks.filter { it.id == selectedProject?.id }.flatMap { it.tasks }
            ExposedDropdownMenu(
                expanded = expandedTask,
                onDismissRequest = { expandedTask = false },
            ) {
                tasks.sortedBy { it.taskId }.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.taskName) },
                        onClick = {
                            selectedTaskName = it.taskName
                            onTaskSelected(it)
                            expandedTask = false
                        },
                        modifier =
                            Modifier.testTag(
                                stringResource(R.string.testTag_putTimeRegistration_taskDropdown_option, it.taskName),
                            ),
                    )
                }
            }
        }
    }
}
