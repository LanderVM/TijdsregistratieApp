package world.inetumrealdolmen.mobiletrm.ui.common.navigation

import android.content.Context
import androidx.annotation.StringRes
import world.inetumrealdolmen.mobiletrm.R

/**
 * Enum class representing navigation routes in your app.
 *
 * @param title The resource ID for the title of the route.
 * @param drawerTitle The resource ID for the title displayed in the navigation drawer (defaults to [title]).
 */
enum class NavigationRoutes(
    @StringRes val title: Int,
    @StringRes val drawerTitle: Int = title,
    val showInDrawer: Boolean = true,
) {
    Home(
        title = R.string.nav_title_projects_overview,
        drawerTitle = R.string.nav_drawerTitle_projects_overview,
        showInDrawer = false,
    ),
    ProjectDetailsOverview(
        title = R.string.nav_title_project_details_overview,
        showInDrawer = false,
    ),
    PutTimeRegistration(
        title = R.string.nav_title_add_time_registration,
        drawerTitle = R.string.nav_drawerTitle_add_time_registration,
    ),
    UpdateTimeRegistration(
        title = R.string.nav_title_update_time_registration,
        showInDrawer = false,
    ),
    Timer(
        title = R.string.nav_title_timer,
        showInDrawer = false,
    ),
    TimeRegistrationsOverview(
        title = R.string.nav_title_time_registrations_overview,
        showInDrawer = false,
    ),
    PutTimeRegistrationTimer(
        title = R.string.nav_title_time_registration_timer,
        showInDrawer = false,
    ),
    CreateGeofence(
        title = R.string.nav_title_create_geofence,
        drawerTitle = R.string.nav_drawerTitle_create_geofence,
    ),
    ;

    /**
     * Get a localized string representation of the route.
     *
     * @param context The Android context.
     * @param isShorthand Whether to use the drawer title (if available) instead of the full title.
     * @return The localized string for the route.
     */
    fun getString(
        context: Context,
        isShorthand: Boolean = false,
    ): String = context.getString(if (isShorthand) drawerTitle else title)
}
