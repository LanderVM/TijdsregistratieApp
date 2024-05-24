package world.inetumrealdolmen.mobiletrm.data.util

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.result.UserProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import world.inetumrealdolmen.mobiletrm.R
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing [Auth0] and [CredentialsManager].
 */
@Module
@InstallIn(SingletonComponent::class)
object Auth0Module {
    /**
     * Provides an instance of Auth0 for authenticating a new user.
     */
    @Provides
    fun providesAuth0(
        @ApplicationContext context: Context,
    ) = Auth0(
        context.getString(R.string.auth0_client_id),
        context.getString(R.string.auth0_domain),
    )

    /**
     * Provides an instance of the Auth0 credentials manager for retrieving access tokens
     */
    @Provides
    fun providesAuth0CredentialsManager(
        @ApplicationContext context: Context,
        auth0: Auth0,
    ) = CredentialsManager(AuthenticationAPIClient(auth0), SharedPreferencesStorage(context))

    /**
     * Defined to allow for testing, will just call CredentialsManager#hasValidCredentials.
     */
    @Provides
    @ValidCredentials
    fun providesHasValidCredentials(credentialsManager: CredentialsManager) = credentialsManager.hasValidCredentials()

    @Provides
    @TestModeValue
    fun providesIsTestMode() = false

    /**
     * Provides the current logged in user's [UserProfile]
     */
    @Provides
    fun providesAuth0User(credentialsManager: CredentialsManager) = runBlocking { credentialsManager.awaitCredentials().user }

    /**
     * Provides the singleton instance of [AuthorizationHeaderInterceptor]
     */
    @Provides
    @Singleton
    fun providesHeaderInterceptor() = AuthorizationHeaderInterceptor()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestModeValue

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ValidCredentials
