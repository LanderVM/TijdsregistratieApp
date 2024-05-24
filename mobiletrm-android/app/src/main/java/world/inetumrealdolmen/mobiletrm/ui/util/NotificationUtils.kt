package world.inetumrealdolmen.mobiletrm.ui.util

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import world.inetumrealdolmen.mobiletrm.R

/**
 * Enum to define the type of notification to display.
 */
enum class NotificationType(
    /**
     * ID to register for Android notification channels. Used to know which notification type to use when displaying a notification.
     */
    val id: Int,
    /**
     * The icon to use in the Android status bar
     */
    @DrawableRes val displayIcon: Int,
    /**
     * The name of the notification channel in the Android permissions menu
     */
    @StringRes val permissionName: Int,
    /**
     * The description of the notification channel in the Android permissions menu
     */
    @StringRes val permissionDescription: Int,
    /**
     * The importance of the notification, indicates whether the notification should be displayed as a pop up, make a sound, ..
     */
    val importance: Int = NotificationManager.IMPORTANCE_LOW,
) {
    TIMER(
        id = 0,
        displayIcon = R.drawable.inetumrealdolmen_notification_logo,
        permissionName = R.string.notifications_timer_displayName,
        permissionDescription = R.string.notifications_timer_description,
    ),
    SYNC(
        id = 1,
        displayIcon = R.drawable.inetumrealdolmen_notification_logo,
        permissionName = R.string.notifications_timer_displayName,
        permissionDescription = R.string.notifications_timer_description,
    ),
    BEACON(
        id = 2,
        displayIcon = R.drawable.inetumrealdolmen_notification_logo,
        permissionName = R.string.notifications_timer_displayName,
        permissionDescription = R.string.notifications_timer_description,
    ),
}

/**
 * Extension function to display an Android notification to the user.
 *
 * @param type The type of notification to use. User must have allowed permission to use certain notifications.
 * @param title The title of the notification.
 * @param ongoing Whether the notification should remain active or not until closed by the app.
 * @param text The inner text of the notification.
 */
fun Context.displayNotification(
    type: NotificationType = NotificationType.TIMER,
    @StringRes title: Int = R.string.notifications_default_title,
    ongoing: Boolean = false,
    @StringRes text: Int,
) {
    val context = this
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }
        notify(
            type.id,
            Builder(context, type.id.toString())
                .setSmallIcon(type.displayIcon)
                .setContentTitle(getString(title))
                .setContentText(getString(text))
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(ongoing).build(),
        )
    }
}

fun Context.displayNotification(
    type: NotificationType = NotificationType.TIMER,
    title: String,
    ongoing: Boolean = false,
    text: String,
) {
    val context = this
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }
        notify(
            type.id,
            Builder(context, type.id.toString())
                .setSmallIcon(type.displayIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(ongoing).build(),
        )
    }
}

/**
 * Clears a notification.
 *
 * @param type The type of notification to clear.
 */
fun Context.cancelNotification(type: NotificationType) = NotificationManagerCompat.from(this).cancel(type.id)

/**
 * Requests for permission to allow notifications
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    val permissionState =
        rememberPermissionState(permission = POST_NOTIFICATIONS)
    var showDialog by remember { mutableStateOf(true) }

    if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale) {
        if (showDialog) {
            // the permission denied once, so show the rationale dialog and request permission
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.notifications_timer_acceptTitle)) },
                text = { Text(stringResource(R.string.notifications_timer_acceptBody)) },
                confirmButton = {
                    Button(
                        onClick = {
                            permissionState.launchPermissionRequest()
                            showDialog = false
                        },
                    ) {
                        Text(stringResource(R.string.notifications_popup_confirmation))
                    }
                },
            )
        }
    } else {
        // permission granted or forever denied
        LaunchedEffect(key1 = Unit, block = { permissionState.launchPermissionRequest() })
        showDialog = false
    }
}

/**
 * Extension function used to create notification channels when launching the app.
 * Must be run before being able to display notifications.
 */
fun Context.createNotificationChannel() {
    // Fetch all notification types we defined earlier and map them to channels for Android
    val channels =
        NotificationType.entries.map {
            NotificationChannel(
                it.id.toString(), // Channel ID
                getString(it.permissionName), // Channel name in the Android permissions menu
                it.importance, // The importance of the channel
            ).apply {
                description = getString(it.permissionDescription) // Channel description in the Android permissions menu
            }
        }

    // Register the channels with the Android notifications system
    val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    channels.forEach(notificationManager::createNotificationChannel)
}
