package world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.ApiCrudState
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.asProject
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavScaffold
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.DateManager
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.DescriptionHeader
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.ModeSwitch
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.ProjectDropdownMenu
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.SaveButton
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.TagsManager
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.TimeEntry
import world.inetumrealdolmen.mobiletrm.ui.screen.registrationtimer.TimerMode
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PutTimeRegistrationScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    existingTimeRegistrationLocalId: UUID? = null,
    viewModel: PutTimeRegistrationViewModel =
        hiltViewModel<PutTimeRegistrationViewModel, PutTimeRegistrationViewModel.AddTimeRegistrationViewModelFactory> {
            it.create(existingTimeRegistrationLocalId)
        },
    context: Context = LocalContext.current,
) {
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.validationResults) {
        if (uiState.validationResults.isNotEmpty()) {
            val newMessage = context.getString(uiState.validationResults[0].type.message)
            if (snackbarHostState.currentSnackbarData?.visuals?.message != newMessage) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(newMessage)
            }
        }
    }

    NavScaffold(
        navController,
        title =
            stringResource(
                id =
                    if (uiState.existingTimeRegistrationRemoteId == -1L) {
                        NavigationRoutes.PutTimeRegistration.title
                    } else {
                        NavigationRoutes.UpdateTimeRegistration.title
                    },
            ),
        modifier = modifier,
        bottomBar = {},
        requireConfirmation = uiState.hasChanged,
        onConfirmation = {
            navController.navigateUp()
            if (navController.currentBackStackEntry?.id == NavigationRoutes.Timer.name) {
                navController.navigateUp()
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    modifier =
                        Modifier
                            .padding(bottom = 64.dp)
                            .testTag(stringResource(R.string.testTag_putTimeRegistration_error)),
                )
            }
        },
        topBarActions = {
            if (existingTimeRegistrationLocalId != null) {
                IconButton(
                    modifier = Modifier.testTag(stringResource(id = R.string.testTag_putTimeRegistration_delete)),
                    onClick = {
                        viewModel.deleteRegistration(uiState.existingTimeRegistrationLocalId!!)
                    },
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.contentDescription_deleteRegistration),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
    ) { padding ->

        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(horizontal = 8.dp),
        ) {
            if (existingTimeRegistrationLocalId == null) {
                ModeSwitch(TimerMode.Manual, {
                    navController.popBackStack(NavigationRoutes.PutTimeRegistration.name, true)
                    navController.navigate(NavigationRoutes.Timer.name)
                })
            }

            when (val state = viewModel.fetchDetailsState) {
                is ApiState.Error ->
                    Indicator(
                        type = IndicatorType.ERROR,
                        error = state.details,
                    )

                ApiState.Loading -> Indicator(IndicatorType.LOADING)
                is ApiState.Success -> {
                    Column(
                        modifier =
                            Modifier
                                .weight(0.89F)
                                .verticalScroll(rememberScrollState()),
                    ) {
                        ProjectDropdownMenu(
                            projectTasks = uiState.projectTasks,
                            onProjectSelected = { id, name -> viewModel.setProject(id, name) },
                            onTaskSelected = { viewModel.setTask(it) },
                            selectedProject =
                                uiState.projectTasks
                                    .firstOrNull { it.id == uiState.project?.id }
                                    ?.asProject(),
                            selectedTask =
                                uiState.projectTasks
                                    .filter { it.id == uiState.project?.id }
                                    .flatMap { it.tasks }
                                    .firstOrNull { it.taskId == uiState.task?.taskId },
                            isEdit = uiState.existingTimeRegistrationRemoteId != -1L,
                            validation = uiState.validationResults.find { it.property == "projectId" },
                        )
                        DescriptionHeader()
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = { viewModel.setDescription(it) },
                            placeholder = {
                                Text(
                                    stringResource(R.string.timeRegistrations_description_placeHolder),
                                    textAlign = TextAlign.Center,
                                )
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            bottomStart = 8.dp,
                                            bottomEnd = 8.dp,
                                        ),
                                    )
                                    .padding(top = 0.dp)
                                    .testTag(stringResource(R.string.testTag_putTimeRegistration_description)),
                            maxLines = 7,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                        )

                        LaunchedEffect(uiState.date.selectedDateMillis) {
                            viewModel.fetchTimeEntries()
                        }

                        DateManager(uiState.date)

                        TimeEntry(
                            timeEntries = uiState.timeEntries,
                            validation =
                                uiState.validationResults
                                    .filter { it.property.contains("timeEntries") }
                                    .toImmutableList(),
                            onAdd = { viewModel.addNewTimeEntry() },
                            showAddButton = existingTimeRegistrationLocalId == null && uiState.timeEntries.size < 4,
                            onChange = { id, startTime, endTime ->
                                viewModel.updateTimeEntry(
                                    id,
                                    startTime,
                                    endTime,
                                )
                            },
                        )

                        TagsManager(
                            tags = uiState.tags,
                            onTagAdded = { viewModel.addTag(it) },
                            showButton = uiState.tags.size < 10,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    LoadAndNavigate(
                        apiCrudState = viewModel.putTimeRegistrationState,
                        navController = navController,
                        showSnackbar = {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                    )
                    SaveButton(
                        text =
                            if (existingTimeRegistrationLocalId == null || uiState.project != null) {
                                R.string.timeRegistrations_saveButton
                            } else {
                                R.string.timeRegistrations_updateButton
                            },
                        onClick = {
                            viewModel.putTimeRegistrations()
                        },
                        isLoading = viewModel.putTimeRegistrationState == ApiCrudState.Loading,
                        isDisabled = uiState.validationResults.isNotEmpty(),
                        modifier =
                            Modifier
                                .weight(0.11F)
                                .padding(vertical = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun LoadAndNavigate(
    apiCrudState: ApiCrudState,
    navController: NavHostController,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (apiCrudState) {
            is ApiCrudState.Error -> showSnackbar(apiCrudState.details.toString())

            ApiCrudState.Start -> {}

            ApiCrudState.Loading ->
                Indicator(
                    type = IndicatorType.LOADING,
                    isIconOnly = true,
                    useEntireScreen = false,
                    iconSize = 16,
                )

            is ApiCrudState.Success -> {
                navController.navigate(
                    NavigationRoutes.TimeRegistrationsOverview.name,
                ) {
                    launchSingleTop = true
                }
            }
        }
    }
}
