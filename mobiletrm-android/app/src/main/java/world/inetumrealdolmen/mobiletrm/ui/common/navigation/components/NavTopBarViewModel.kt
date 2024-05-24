package world.inetumrealdolmen.mobiletrm.ui.common.navigation.components

import androidx.lifecycle.ViewModel
import com.auth0.android.authentication.storage.CredentialsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import world.inetumrealdolmen.mobiletrm.beacons.BeaconManagerSetup
import javax.inject.Inject

@HiltViewModel
class NavTopBarViewModel
    @Inject
    constructor(
        private val credentialsManager: CredentialsManager,
        private val beaconManager: BeaconManagerSetup,
    ) :
    ViewModel() {
        fun logOut() {
            credentialsManager.clearCredentials()
            beaconManager.stop()
        }
    }
