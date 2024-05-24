package world.inetumrealdolmen.mobiletrm.util

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.result.UserProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.util.Auth0Module
import world.inetumrealdolmen.mobiletrm.data.util.AuthorizationHeaderInterceptor
import world.inetumrealdolmen.mobiletrm.data.util.TestModeValue
import world.inetumrealdolmen.mobiletrm.data.util.ValidCredentials
import javax.inject.Singleton

/**
 * Auth0Module mock for Hilt to inject in instrumented tests.
 */
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [Auth0Module::class],
)
@Module
object FakeAuth0Module {
    /**
     * Provides an instance of [Auth0]
     */
    @Provides
    fun providesAuth0(
        @ApplicationContext context: Context,
    ) = Auth0(
        context.getString(R.string.auth0_client_id),
        context.getString(R.string.auth0_domain),
    )

    /**
     * Provides an instance of [CredentialsManager].
     */
    @Provides
    fun providesAuth0CredentialsManager(
        @ApplicationContext context: Context,
        auth0: Auth0,
    ) = CredentialsManager(AuthenticationAPIClient(auth0), SharedPreferencesStorage(context))

    /**
     * Provides a fake [UserProfile] object
     */
    @Provides
    fun providesAuth0User() =
        UserProfile(
            id = null,
            name = "TestUser",
            nickname = null,
            pictureURL = null,
            email = null,
            isEmailVerified = null,
            familyName = null,
            createdAt = null,
            identities = listOf(),
            extraInfo = null,
            userMetadata = mapOf(),
            appMetadata = mapOf(),
            givenName = "TestUser",
        )

    /**
     * Provides a mocked interceptor singleton
     */
    @Singleton
    @Provides
    fun providesInterceptor(): AuthorizationHeaderInterceptor {
        val interceptor = AuthorizationHeaderInterceptor()
        interceptor.token = "test_token"
        return interceptor
    }

    /**
     * Will make sure the application thinks the user is logged in upon starting during testing
     */
    @Provides
    @ValidCredentials
    fun providesHasValidCredentials() = true

    /**
     * For testing.
     */
    @Provides
    @TestModeValue
    fun providesTestMode() = true
}
