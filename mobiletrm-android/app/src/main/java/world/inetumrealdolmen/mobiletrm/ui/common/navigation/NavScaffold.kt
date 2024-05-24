package world.inetumrealdolmen.mobiletrm.ui.common.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.auth0.android.result.UserProfile
import kotlinx.coroutines.launch
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.components.NavBottomBar
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.components.NavDrawer
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.components.NavTopBar

/**
 * Composable that represents the base scaffold to be used in screens, based on [Scaffold].
 * Comes with a top navigation bar and optional bottom navigation bar.
 * The bottom navigation bar uses [NavBottomBar] by default if there is none supplied.
 *
 * @param navController NavHostController for navigation.
 * @param modifier Modifier for custom styling.
 * @param title The title text to display in the top navigation bar.
 * @param bottomBar Optional composable to replace the default [NavBottomBar]
 * @param snackbarHost The snackbar host to display any snackbars. Optional unless snackbars should be shown.
 * @param requireConfirmation Whether a confirmation dialog should appear when the user wants to leave the screen.
 * @param onConfirmation The action to perform when the user confirms leaving the screen.
 * @param userInfo The user's information that's retrieved when logging in to show.
 * @param topBarActions The buttons to display next to the title in the top navigation bar.
 * @param content The content to display within the [NavScaffold], typically the contents of the page.
 */
@Composable
fun NavScaffold(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    title: String? = null,
    bottomBar:
        @Composable
        (() -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    requireConfirmation: Boolean = false,
    onConfirmation: () -> Unit = {},
    userInfo: UserProfile? = null,
    topBarActions: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    // Handling system back button
    BackHandler(enabled = requireConfirmation && showDialog.not()) {
        showDialog = true
    }

    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerContent = {
            NavDrawer(
                context = context,
                onClick = {
                    if (it == NavigationRoutes.Home.name) navController.popBackStack()
                    navController.navigate(it)
                    scope.launch {
                        drawerState.close()
                    }
                },
            )
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                NavTopBar(
                    title =
                        title
                            ?: NavigationRoutes.valueOf(
                                backStackEntry?.destination?.route?.substringBefore("/")
                                    ?: NavigationRoutes.Home.name,
                            ).getString(context, false),
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = {
                        if (requireConfirmation) {
                            showDialog = true
                        } else {
                            navController.navigateUp()
                        }
                    },
                    openDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    userInfo = userInfo,
                ) {
                    topBarActions()
                }
            },
            bottomBar = { bottomBar ?: NavBottomBar(navController) },
            snackbarHost = snackbarHost,
        ) { innerPadding ->
            content(innerPadding)
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirm") },
                    text = { Text("Are you sure you want to go back?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                onConfirmation()
                            },
                            modifier =
                                Modifier.testTag(
                                    stringResource(
                                        R.string.testTag_putTimeRegistration_confirmation_confirmButton,
                                    ),
                                ),
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false },
                        ) {
                            Text("No")
                        }
                    },
                )
            }
        }
    }
}
