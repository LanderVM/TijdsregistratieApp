package world.inetumrealdolmen.mobiletrm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes
import world.inetumrealdolmen.mobiletrm.ui.screen.geofence.GeofenceScreen
import world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.ProjectDetailsScreen
import world.inetumrealdolmen.mobiletrm.ui.screen.projects.ProjectsScreen
import world.inetumrealdolmen.mobiletrm.ui.screen.puttimeregistration.PutTimeRegistrationScreen
import world.inetumrealdolmen.mobiletrm.ui.screen.registrationtimer.RegistrationTimerScreen
import world.inetumrealdolmen.mobiletrm.ui.screen.timeregistrationsoverview.TimeRegistrationsOverviewScreen
import java.util.UUID

/**
 * Main entry point for the application UI.
 *
 * @param modifier Modifier for custom styling.
 * @param navController The navigation controller for managing navigation between screens.
 */
@Composable
fun Application(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavigationRoutes.Home.name,
    ) {
        composable(NavigationRoutes.Home.name) {
            ProjectsScreen(navController)
        }
        composable(NavigationRoutes.PutTimeRegistration.name) {
            PutTimeRegistrationScreen(navController)
        }
        composable(NavigationRoutes.UpdateTimeRegistration.name + "/{timeRegistrationId}") {
            PutTimeRegistrationScreen(
                navController,
                existingTimeRegistrationLocalId = UUID.fromString(it.arguments?.getString("timeRegistrationId")),
            )
        }
        composable(NavigationRoutes.Timer.name) {
            RegistrationTimerScreen(navController)
        }
        composable(NavigationRoutes.TimeRegistrationsOverview.name) {
            TimeRegistrationsOverviewScreen(navController)
        }
        composable(NavigationRoutes.ProjectDetailsOverview.name + "/{projectId}") {
            ProjectDetailsScreen(
                navController,
                it.arguments?.getString("projectId")?.toLong() ?: -1,
            )
        }
        composable(NavigationRoutes.CreateGeofence.name) {
            GeofenceScreen(navController)
        }
    }
}
