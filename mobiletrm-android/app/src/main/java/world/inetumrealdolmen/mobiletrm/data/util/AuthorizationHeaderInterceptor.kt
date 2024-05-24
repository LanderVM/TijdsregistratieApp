package world.inetumrealdolmen.mobiletrm.data.util

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add authorization bearer token to every request to the API.
 * Set the [token] before making API calls.
 */
class AuthorizationHeaderInterceptor : Interceptor {
    lateinit var token: String

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build(),
        )
}
