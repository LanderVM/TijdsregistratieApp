package world.inetumrealdolmen.mobiletrm.data.util

import android.content.Context
import androidx.lifecycle.LiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import world.inetumrealdolmen.mobiletrm.beacons.BeaconManagerSetup
import world.inetumrealdolmen.mobiletrm.data.local.MobileTRMDao
import world.inetumrealdolmen.mobiletrm.data.local.PreferencesDao
import world.inetumrealdolmen.mobiletrm.data.remote.network.ApiService
import world.inetumrealdolmen.mobiletrm.data.remote.network.ConnectionState
import world.inetumrealdolmen.mobiletrm.data.repository.MobileTRMRepository
import world.inetumrealdolmen.mobiletrm.data.repository.PreferencesRepository
import world.inetumrealdolmen.mobiletrm.data.repository.impl.PreferencesRepositoryImpl
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing general application dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides an instance of [ApiService].
     *
     * @return The configured [ApiService] instance.
     */
    @Provides
    fun providesApiService(authorizationHeaderInterceptor: AuthorizationHeaderInterceptor): ApiService {
        val restApiBaseUrl = "http://quarkus-app.westeurope.azurecontainer.io:8080/api/"
//        val restApiBaseUrl = "http://192.168.178.27:8080/"
        val logger =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val clientBuilder =
            OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(authorizationHeaderInterceptor)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()

        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            )
            .baseUrl(restApiBaseUrl)
            .client(clientBuilder)
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Provides an instance of [MobileTRMRepository].
     *
     * @param api The [ApiService] instance.
     * @param dao The [MobileTRMDao] instance.
     * @param connection The user's connection state to the internet.
     * @return The repository for managing project data.
     */
    @Provides
    @Singleton
    fun providesRepository(
        api: ApiService,
        dao: MobileTRMDao,
        connection: LiveData<ConnectionState>,
        @ApplicationContext context: Context,
    ): MobileTRMRepository = QuarkusRepository(api, dao, connection, context)

    /**
     * Provides an instance of [MobileTRMRepository].
     *
     * @param dao The [PreferencesDao] instance.
     * @return The repository for managing user preferences.
     */
    @Provides
    fun providesUserPreferencesRepository(dao: PreferencesDao): PreferencesRepository = PreferencesRepositoryImpl(dao)

    /**
     * Provides an instance of [BeaconManagerSetup] for workspace detection.
     */
    @Provides
    @Singleton
    fun providesBeaconManager(
        @ApplicationContext context: Context,
    ) = BeaconManagerSetup(
        context,
    )
}
