package world.inetumrealdolmen.mobiletrm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import world.inetumrealdolmen.mobiletrm.beacons.BeaconManagerSetup
import world.inetumrealdolmen.mobiletrm.data.local.LocalDatabase
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType
import world.inetumrealdolmen.mobiletrm.data.util.AuthorizationHeaderInterceptor
import world.inetumrealdolmen.mobiletrm.data.util.TestModeValue
import world.inetumrealdolmen.mobiletrm.data.util.ValidCredentials
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.theme.MobileTRMTheme
import world.inetumrealdolmen.mobiletrm.ui.util.createNotificationChannel
import javax.inject.Inject

/**
 * Main activity for the MobileTRM app.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var account: Auth0

    @Inject
    lateinit var credentialsManager: CredentialsManager

    @Inject
    lateinit var authorizationHeaderInterceptor: AuthorizationHeaderInterceptor

    @JvmField
    @Inject
    @ValidCredentials
    var hasValidCredentials: Boolean = true // allows for testing

    @JvmField
    @Inject
    @TestModeValue
    var isTestMode: Boolean = false // allows for testing

    var loginState: ApiState by mutableStateOf(ApiState.Loading)

    @Inject
    lateinit var database: LocalDatabase

    @Inject
    lateinit var beaconManagerSetup: BeaconManagerSetup

    private val requiredPermissions =
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.POST_NOTIFICATIONS,
        )

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val allPermissionsGranted = permissions.entries.all { it.value }
            if (allPermissionsGranted) {
                initializeBeaconManager()
            } else {
                // Handle the case where the user denies the permissions
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasValidCredentials) {
            loginState = ApiState.Success
        } else {
            loginWithBrowser()
        }

        setAppContent {
            when (loginState) {
                ApiState.Loading -> {
                    if (!hasValidCredentials) {
                        LaunchedEffect(Unit) {
                            withContext(Dispatchers.IO) {
                                database.clearAllTables()
                            }
                        }
                    }
                    Indicator(type = IndicatorType.LOADING)
                }
                is ApiState.Error -> LoggedOutContent()
                ApiState.Success -> {
                    if (!isTestMode) {
                        authorizationHeaderInterceptor.token =
                            runBlocking { credentialsManager.awaitCredentials().accessToken }
                    }
                    Application()
                    createNotificationChannel()

                    // Request necessary permissions
                    if (!allPermissionsGranted()) {
                        requestPermissionLauncher.launch(requiredPermissions)
                    } else {
                        initializeBeaconManager()
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initializeBeaconManager() {
        beaconManagerSetup.initialize()
    }

    // Initiate logging in using auth0 universal login webview
    private fun loginWithBrowser() {
        // Start auth0 webview
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.auth0_scheme))
            .withScope("openid profile email offline_access")
            .start(
                this,
                object : Callback<Credentials, AuthenticationException> {
                    override fun onFailure(error: AuthenticationException) {
                        credentialsManager.clearCredentials()
                        authorizationHeaderInterceptor.token = ""

                        loginState = ApiState.Error(ErrorType.Unmapped)
                    }

                    override fun onSuccess(result: Credentials) {
                        credentialsManager.saveCredentials(result)
                        authorizationHeaderInterceptor.token = result.accessToken

                        loginState = ApiState.Success
                    }
                },
            )
    }

    // Show an indicator to ask the user to log in first
    @Composable
    private fun LoggedOutContent() {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Indicator(
                type = IndicatorType.INFO,
                text = stringResource(R.string.auth_message_mustBeLoggedIn),
                modifier = Modifier.weight(0.8F),
            )
            Button(
                modifier = Modifier.padding(bottom = 32.dp),
                onClick = ::loginWithBrowser,
            ) {
                Text(stringResource(R.string.auth_button_login))
            }
        }
    }

    private fun setAppContent(content: @Composable () -> Unit) {
        setContent {
            MobileTRMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    content()
                }
            }
        }
    }
}
