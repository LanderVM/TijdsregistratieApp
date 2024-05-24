package world.inetumrealdolmen.mobiletrm.ui.util

import android.util.Log
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import world.inetumrealdolmen.mobiletrm.data.model.ApiCrudState
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 * Parses an incoming exception and maps it to an [ErrorType] for [ApiState].
 *
 * @param e The exception to parse.
 * @param func The name of the function to log.
 * @return an [ApiState] object containing the applicable [ErrorType]
 */
fun parseException(
    e: Exception,
    func: String,
): ApiState =
    when (e) {
        // Allow viewmodel coroutine scopes to be handled by Android
        is CancellationException -> throw e

        // Parse responses from API
        is HttpException -> {
            Log.e(func, e.message ?: e.toString())
            ApiState.Error(
                when (e.code()) {
                    400 -> ErrorType.BadRequest
                    404 -> ErrorType.NotFound
                    500 -> ErrorType.Internal
                    else -> ErrorType.Unknown(e.message)
                },
            )
        }

        // Handle API connection failed
        is SocketTimeoutException, is SocketException -> {
            Log.e(func, e.message ?: e.toString())
            ApiState.Error(ErrorType.SocketTimeout)
        }

        else -> {
            Log.e(func, e.toString())
            ApiState.Error(ErrorType.Unmapped)
        }
    }

/**
 * Parses an incoming exception and maps it to an [ErrorType] for [ApiCrudState].
 *
 * @param e The exception to parse.
 * @param func The name of the function to log.
 * @return an [ApiCrudState] object containing the applicable [ErrorType]
 */
fun parsePutException(
    e: Exception,
    func: String,
): ApiCrudState =
    when (e) {
        // Allow viewmodel coroutine scopes to be handled by Android
        is CancellationException -> throw e

        // Parse responses from API
        is HttpException -> {
            Log.e(func, e.message ?: e.toString())
            ApiCrudState.Error(
                when (e.code()) {
                    400 -> ErrorType.BadRequest
                    404 -> ErrorType.NotFound
                    500 -> ErrorType.Internal
                    else -> ErrorType.Unknown(e.message)
                },
            )
        }

        // Handle API connection failed
        is SocketTimeoutException, is SocketException -> {
            Log.e(func, e.message ?: e.toString())
            ApiCrudState.Error(ErrorType.SocketTimeout)
        }

        else -> {
            Log.e(func, e.stackTraceToString())
            ApiCrudState.Error(ErrorType.Unmapped)
        }
    }
