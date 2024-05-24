package world.inetumrealdolmen.mobiletrm.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import world.inetumrealdolmen.mobiletrm.data.remote.network.ConnectionState
import world.inetumrealdolmen.mobiletrm.data.remote.network.currentConnectivityState
import world.inetumrealdolmen.mobiletrm.data.remote.network.observeConnectivityAsFlow

/**
 * Observe the connectivity state as a Composable State.
 * Emits [ConnectionState] updates as a State<ConnectionState> when the network state changes.
 *
 * @return A State<ConnectionState> representing the network connectivity state.
 */
@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectivityState) {
        context.observeConnectivityAsFlow().collect { value = it }
    }
}
