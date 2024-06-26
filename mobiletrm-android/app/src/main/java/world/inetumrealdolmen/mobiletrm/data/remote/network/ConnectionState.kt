package world.inetumrealdolmen.mobiletrm.data.remote.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Represents the state of network connectivity.
 */
sealed class ConnectionState {
    /**
     * Represents the state when the network connection is available.
     */
    data object Available : ConnectionState()

    /**
     * Represents the state when the network connection is unavailable.
     */
    data object Unavailable : ConnectionState()
}

/**
 * Network utility to get current state of internet connection
 */
val Context.currentConnectivityState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

/**
 * Gets the current network connectivity state based on the provided [ConnectivityManager].
 *
 * @param connectivityManager The ConnectivityManager instance to check network state.
 * @return [ConnectionState.Available] if the network connection is available, [ConnectionState.Unavailable] otherwise.
 */
fun getCurrentConnectivityState(connectivityManager: ConnectivityManager?): ConnectionState {
    val network = connectivityManager?.activeNetwork ?: return ConnectionState.Unavailable
    val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return ConnectionState.Unavailable
    return when {
        actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionState.Available
        actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionState.Available
        else -> ConnectionState.Unavailable
    }
}

/**
 * Network Utility to observe the availability or unavailability of the Internet connection as a Flow.
 * Emits [ConnectionState] updates when the network state changes.
 *
 * @return A Flow of [ConnectionState] representing the network connectivity state.
 */
@ExperimentalCoroutinesApi
fun Context.observeConnectivityAsFlow(): Flow<ConnectionState> =
    callbackFlow {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Callback for network changes
        val callback = NetworkCallback { connectionState -> trySend(connectionState) }
        val networkRequest =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        // Register network callback
        connectivityManager.registerNetworkCallback(networkRequest, callback)

        // Set current state
        val currentState = getCurrentConnectivityState(connectivityManager)
        trySend(currentState)

        // Remove callback when not used
        awaitClose {
            // Remove listeners
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

/**
 * Creates a NetworkCallback to handle network changes.
 *
 * @param callback The callback to be invoked when the network state changes.
 * @return A ConnectivityManager.NetworkCallback object.
 */
fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }
    }
}
