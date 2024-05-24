package world.inetumrealdolmen.mobiletrm.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Android runner to use for running instrumentation tests with Hilt dependency injection.
 */
@Suppress("unused") // Used in testInstrumentationRunner config in app-level Gradle
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
