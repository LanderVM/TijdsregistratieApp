package world.inetumrealdolmen.mobiletrm.ui.util

import android.content.Context
import android.content.Intent
import world.inetumrealdolmen.mobiletrm.MainActivity

/**
 * Used to restart the application.
 */
fun Context.restart() {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}
