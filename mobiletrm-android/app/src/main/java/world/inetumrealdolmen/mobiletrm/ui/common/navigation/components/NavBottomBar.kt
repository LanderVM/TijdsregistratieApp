package world.inetumrealdolmen.mobiletrm.ui.common.navigation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes

/**
 * Composable that represents a navigation bottom bar.
 *
 * @param navController NavHostController for navigation.
 * @param modifier Modifier for custom styling.
 */
@Composable
fun NavBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar(
        modifier = modifier,
    ) {
        NavigationBarItem(
            onClick = {
                if (backStackEntry?.destination?.route?.substringBefore("/")
                    == NavigationRoutes.Home.name
                ) {
                    return@NavigationBarItem
                }
                navController.popBackStack(NavigationRoutes.Home.name, true)
                navController.navigate(NavigationRoutes.Home.name)
            },
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = stringResource(R.string.contentDescription_bottomNavigateHome),
                    modifier = Modifier.size(32.dp),
                )
            },
            selected = false,
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .testTag(stringResource(R.string.testTag_bottomNav_navigateHome)),
        )
        NavigationBarItem(
            onClick = { navController.navigate(NavigationRoutes.Timer.name) },
            icon = {
                Button(
                    { navController.navigate(NavigationRoutes.Timer.name) },
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.contentDescription_bottomNavigateAddTimeRegistration),
                        modifier = Modifier.size(32.dp),
                    )
                }
            },
            selected = false,
            modifier = Modifier.testTag(stringResource(R.string.testTag_bottomNav_navigateAddTimeRegistration)),
        )
        NavigationBarItem(
            onClick = {
                if (backStackEntry?.destination?.route?.substringBefore("/")
                    == NavigationRoutes.TimeRegistrationsOverview.name
                ) {
                    return@NavigationBarItem
                }
                navController.navigate(NavigationRoutes.TimeRegistrationsOverview.name)
            },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.contentDescription_bottomNavigateTimeRegistrationOverview),
                    modifier = Modifier.size(48.dp),
                )
            },
            selected = false,
            modifier =
                Modifier.padding(horizontal = 12.dp)
                    .testTag(stringResource(R.string.testTag_bottomNav_navigateTimeRegistrationOverview)),
        )
    }
}
