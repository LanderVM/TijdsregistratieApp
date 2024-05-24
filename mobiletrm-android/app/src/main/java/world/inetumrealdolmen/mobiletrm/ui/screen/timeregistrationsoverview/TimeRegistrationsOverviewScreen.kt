package world.inetumrealdolmen.mobiletrm.ui.screen.timeregistrationsoverview

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toJavaLocalDate
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavScaffold
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes
import world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.components.ProjectTimeRegistrations
import world.inetumrealdolmen.mobiletrm.ui.theme.onSuccessDark
import world.inetumrealdolmen.mobiletrm.ui.theme.onSuccessLight
import world.inetumrealdolmen.mobiletrm.ui.theme.successDark
import world.inetumrealdolmen.mobiletrm.ui.theme.successLight
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * A screen displaying an overview of time registrations. It allows users to filter
 * time registrations by day, week, or month and navigate through them. Users can also
 * edit or delete time registrations.
 *
 * @param navController Controls navigation among composables.
 * @param modifier Modifiers to apply to this screen's layout.
 * @param viewModel ViewModel that holds the state and business logic for this screen.
 * @param context The application's context. Default is [LocalContext]'s current context.
 */
@Composable
fun TimeRegistrationsOverviewScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: TimeRegistrationsOverviewViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    NavScaffold(
        navController,
        title = stringResource(id = NavigationRoutes.TimeRegistrationsOverview.title),
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = if (isSystemInDarkTheme()) successLight else successDark,
                    contentColor = if (isSystemInDarkTheme()) onSuccessLight else onSuccessDark,
                )
            }
        },
    ) { padding ->

        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(horizontal = 8.dp),
        ) {
            when (val state = viewModel.filterState) {
                is ApiState.Error -> Indicator(IndicatorType.ERROR, error = state.details)
                ApiState.Loading -> Indicator(IndicatorType.LOADING)
                ApiState.Success -> {
                    val snackbar by viewModel.snackbar.collectAsState()

                    // Show snackbar
                    LaunchedEffect(snackbar) {
                        if (snackbar.timeRegistrationCreated) {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.timeRegistration_successCreated))
                            }
                        }
                        if (snackbar.timeRegistrationUpdated) {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.timeRegistration_successUpdated))
                            }
                        }
                        if (snackbar.timeRegistrationDeleted) {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.timeRegistration_successDeleted))
                            }
                        }
                        viewModel.resetSnackbar()
                    }

                    FilterComposable(
                        selectedFilter = viewModel.selectedFilter,
                        onFilterSelected = viewModel::onFilter,
                    )
                    FilterNavigation(
                        selectedFilter = viewModel.selectedFilter,
                        referenceDate = viewModel.referenceDate.collectAsState(),
                        onNext = { viewModel.navigateToNext() },
                        onPrevious = { viewModel.navigateToPrevious() },
                    )
                }
            }

            when (val state = viewModel.registrationsState) {
                is ApiState.Error -> Indicator(IndicatorType.ERROR, error = state.details)
                ApiState.Loading -> Indicator(IndicatorType.LOADING)
                is ApiState.Success -> {
                    ProjectTimeRegistrations(
                        times = uiState.timeRegistrations.toImmutableList(),
                        canModify = true,
                        onDelete = {
                            viewModel.deleteTimeRegistration(it)
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.timeRegistration_successDeleted))
                            }
                        },
                        onEdit = { navController.navigate("${NavigationRoutes.UpdateTimeRegistration.name}/$it") },
                        runningProjects = uiState.runningProjectIds,
                    )
                }
            }
        }
    }
}

/**
 * Composable function that displays filter options for time registrations. Users can
 * select a filter to apply to the list of time registrations.
 *
 * @param selectedFilter The currently selected filter type.
 * @param onFilterSelected Callback invoked when a new filter is selected.
 * @param modifier Modifiers to apply to this composable's layout.
 */
@Composable
fun FilterComposable(
    selectedFilter: StateFlow<FilterType>,
    onFilterSelected: (FilterType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentFilter = selectedFilter.collectAsState().value

    val startShape =
        RoundedCornerShape(
            topStartPercent = 25,
            bottomStartPercent = 25,
            topEndPercent = 0,
            bottomEndPercent = 0,
        )
    val middleShape = RoundedCornerShape(ZeroCornerSize)
    val endShape =
        RoundedCornerShape(
            topStartPercent = 0,
            bottomStartPercent = 0,
            topEndPercent = 25,
            bottomEndPercent = 25,
        )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
    ) {
        FilterType.entries.forEach { filter ->
            val isSelected = filter == currentFilter
            val shape: Shape =
                when (filter) {
                    FilterType.Day -> startShape
                    FilterType.Week -> middleShape
                    FilterType.Month -> endShape
                }
            TextButton(
                onClick = {
                    onFilterSelected(filter)
                },
                shape = shape,
                colors =
                    ButtonDefaults.textButtonColors(
                        containerColor =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                        contentColor =
                            if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                    ),
                modifier =
                    Modifier
                        .weight(1f),
            ) {
                Text(
                    text = filter.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}

/**
 * Composable function that provides navigation buttons to move through filtered time registrations.
 * Displays the current filter context (e.g., date range) and allows users to navigate to the previous
 * or next period as per the selected filter.
 *
 * @param modifier Modifiers to apply to this composable's layout.
 * @param selectedFilter The currently selected filter type.
 * @param referenceDate The reference date used for filtering.
 * @param onPrevious Callback invoked when the user selects the previous period.
 * @param onNext Callback invoked when the user selects the next period.
 */
@Composable
fun FilterNavigation(
    selectedFilter: StateFlow<FilterType>,
    referenceDate: State<kotlinx.datetime.LocalDate>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayText =
        when (selectedFilter.collectAsState().value) {
            FilterType.Day ->
                referenceDate.value.toJavaLocalDate()
                    .format(DateTimeFormatter.ofPattern("EEE dd MMM"))

            FilterType.Week -> {
                val startOfWeek =
                    referenceDate.value.toJavaLocalDate()
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endOfWeek =
                    referenceDate.value.toJavaLocalDate()
                        .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                "${
                    startOfWeek.format(
                        DateTimeFormatter.ofPattern("EEE dd MMM"),
                    )
                } - ${endOfWeek.format(DateTimeFormatter.ofPattern("EEE dd MMM"))}"
            }

            FilterType.Month ->
                referenceDate.value.toJavaLocalDate()
                    .format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.contentDescription_previousIcon),
            )
        }
        Text(displayText)
        IconButton(onClick = onNext) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.contentDescription_nextIcon),
            )
        }
    }
}
