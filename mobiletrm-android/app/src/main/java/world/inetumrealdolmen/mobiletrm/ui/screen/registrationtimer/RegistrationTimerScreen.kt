package world.inetumrealdolmen.mobiletrm.ui.screen.registrationtimer

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.ApiCrudState
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavScaffold
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.components.ModeSwitch
import world.inetumrealdolmen.mobiletrm.ui.theme.onSuccessDark
import world.inetumrealdolmen.mobiletrm.ui.theme.onSuccessLight
import world.inetumrealdolmen.mobiletrm.ui.theme.successDark
import world.inetumrealdolmen.mobiletrm.ui.theme.successLight
import world.inetumrealdolmen.mobiletrm.ui.util.NotificationType
import world.inetumrealdolmen.mobiletrm.ui.util.RequestNotificationPermission
import world.inetumrealdolmen.mobiletrm.ui.util.cancelNotification
import world.inetumrealdolmen.mobiletrm.ui.util.displayNotification
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun RegistrationTimerScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: RegistrationTimerViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
) {
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val elapsedTimeDisplay =
        remember(uiState.elapsedTime) {
            val hours = TimeUnit.MILLISECONDS.toHours(uiState.elapsedTime.toLong())
            val minutes = TimeUnit.MILLISECONDS.toMinutes(uiState.elapsedTime.toLong()) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(uiState.elapsedTime.toLong()) % 60
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermission()
    }

    LaunchedEffect(viewModel.timerState) {
        when (val state = viewModel.timerState) {
            is ApiCrudState.Error ->
                snackbarHostState.showSnackbar(
                    message =
                        if (state.details !is ErrorType.Unknown) {
                            context.getString(
                                R.string.error_unknown,
                            )
                        } else {
                            state.details.errorMessage ?: ""
                        },
                )

            else -> {}
        }
    }

    NavScaffold(
        navController,
        title = stringResource(id = NavigationRoutes.Timer.title),
        modifier = modifier,
        bottomBar = {},
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor =
                        if (viewModel.timerState is ApiCrudState.Error) {
                            MaterialTheme.colorScheme.error
                        } else {
                            if (isSystemInDarkTheme()) successLight else successDark
                        },
                    contentColor =
                        if (viewModel.timerState is ApiCrudState.Error) {
                            MaterialTheme.colorScheme.onError
                        } else {
                            if (isSystemInDarkTheme()) onSuccessLight else onSuccessDark
                        },
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
            ModeSwitch(TimerMode.Timer, {
                navController.popBackStack(NavigationRoutes.Timer.name, true)
                navController.navigate(NavigationRoutes.PutTimeRegistration.name)
            })

            Column(
                modifier =
                    Modifier
                        .weight(1F)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.timeRegistration_timerTitle),
                    fontSize = 32.sp,
                    modifier =
                        Modifier.padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = elapsedTimeDisplay,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(16.dp),
                            )
                            .padding(horizontal = 32.dp, vertical = 16.dp),
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (uiState.canDiscard || viewModel.timerState is ApiCrudState.Error) {
                        IconButton(
                            onClick = {
                                viewModel.discardCurrentTimer()
                                scope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.timeRegistration_timer_discarded))
                                }
                                context.cancelNotification(NotificationType.TIMER)
                            },
                            modifier =
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .padding(4.dp)
                                    .size(64.dp),
                        ) {
                            Icon(
                                painterResource(R.drawable.delete_forever),
                                contentDescription = stringResource(R.string.contentDescription_stopIcon),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp),
                            )
                        }
                    }
                    when (!uiState.canPause) {
                        true ->
                            IconButton(
                                onClick = {
                                    if (uiState.pauses >= RegistrationTimerViewModel.MAX_PAUSES) {
                                        Toast.makeText(
                                            context,
                                            "You can only pause up to 3 times!",
                                            Toast.LENGTH_LONG,
                                        ).show()
                                    } else {
                                        viewModel.startTimer()
                                        context.displayNotification(
                                            NotificationType.TIMER,
                                            title = R.string.notifications_timer_running_title,
                                            ongoing = true,
                                            text = R.string.notifications_timer_running_text,
                                        )
                                    }
                                },
                                modifier =
                                    Modifier
                                        .background(
                                            if (uiState.pauses < RegistrationTimerViewModel.MAX_PAUSES) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7F)
                                            },
                                            CircleShape,
                                        )
                                        .padding(4.dp)
                                        .size(64.dp),
                            ) {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = stringResource(R.string.contentDescription_startIcon),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(40.dp),
                                )
                            }

                        false ->
                            IconButton(
                                onClick = {
                                    viewModel.pauseTimer()
                                    context.displayNotification(
                                        NotificationType.TIMER,
                                        title = R.string.notifications_timer_paused_title,
                                        ongoing = true,
                                        text = R.string.notifications_timer_paused_text,
                                    )
                                },
                                enabled = uiState.pauses < RegistrationTimerViewModel.MAX_PAUSES + 1,
                                modifier =
                                    Modifier
                                        .background(
                                            if (uiState.pauses < RegistrationTimerViewModel.MAX_PAUSES) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7F)
                                            },
                                            CircleShape,
                                        )
                                        .padding(4.dp)
                                        .size(64.dp),
                            ) {
                                Icon(
                                    painterResource(R.drawable.pause),
                                    contentDescription = stringResource(R.string.contentDescription_pauseIcon),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(40.dp),
                                )
                            }
                    }

                    if (uiState.canSubmit) {
                        IconButton(
                            onClick = {
                                viewModel.putTimeRegistrations()
                                context.cancelNotification(NotificationType.TIMER)
                            },
                            modifier =
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .padding(4.dp)
                                    .size(64.dp),
                        ) {
                            Icon(
                                painterResource(R.drawable.baseline_check_24),
                                contentDescription = stringResource(R.string.contentDescription_submitIcon),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp),
                            )
                        }
                    }
                }
            }

            when (val state = viewModel.apiCrudState) {
                is ApiCrudState.Error ->
                    Indicator(
                        type = IndicatorType.ERROR,
                        error = state.details,
                    )

                ApiCrudState.Loading -> Indicator(type = IndicatorType.LOADING, isIconOnly = true)
                ApiCrudState.Start -> {}
                is ApiCrudState.Success -> {
                    navController.navigate(NavigationRoutes.TimeRegistrationsOverview.name) {
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}
