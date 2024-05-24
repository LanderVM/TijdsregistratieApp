package world.inetumrealdolmen.mobiletrm.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import world.inetumrealdolmen.mobiletrm.data.local.LocalDatabase
import world.inetumrealdolmen.mobiletrm.data.remote.network.ConnectionState
import world.inetumrealdolmen.mobiletrm.data.remote.network.getCurrentConnectivityState
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing RoomDB local storage related objects.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {
    /**
     * Provides a [LiveData] of the user's current [ConnectionState].
     *
     * @param context The application's context.
     */
    @Provides
    @Singleton
    fun provideConnectivityState(
        @ApplicationContext context: Context,
    ): LiveData<ConnectionState> {
        val liveData = MutableLiveData<ConnectionState>()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Callback for network changes
        val callback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    liveData.postValue(ConnectionState.Available)
                }

                override fun onLost(network: Network) {
                    liveData.postValue(ConnectionState.Unavailable)
                }
            }

        val networkRequest =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        // Register network callback
        connectivityManager.registerNetworkCallback(networkRequest, callback)

        // Set current state
        val currentState = getCurrentConnectivityState(connectivityManager)
        liveData.postValue(currentState)

        // Return LiveData
        return liveData
    }

    /**
     * Provides a singleton instance of [LocalDatabase] to inject into dao's.
     *
     * @param context The application's context.
     */
    @Provides
    @Singleton
    fun providesRoomDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        "mobiletrm-database",
    )
        .enableMultiInstanceInvalidation()
        .fallbackToDestructiveMigration()
        .build()

    /**
     * Provides a Room DAO for locally cached items.
     *
     * @param localDb Room's [LocalDatabase] instance to query in.
     */
    @Provides
    @Singleton
    fun provideLocalCacheDao(localDb: LocalDatabase) = localDb.dao()

    /**
     * Provides a Room DAO for user's preferences.
     *
     * @param localDb Room's [LocalDatabase] instance to query in.
     */
    @Provides
    @Singleton
    fun providesUserPreferencesDao(localDb: LocalDatabase) = localDb.preferencesDao()
}
