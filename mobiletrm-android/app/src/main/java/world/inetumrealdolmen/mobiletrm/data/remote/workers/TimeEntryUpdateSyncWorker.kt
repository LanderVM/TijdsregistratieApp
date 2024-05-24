package world.inetumrealdolmen.mobiletrm.data.remote.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.auth0.android.authentication.storage.CredentialsManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationUpdateDTO
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.dto.asDto
import world.inetumrealdolmen.mobiletrm.data.local.MobileTRMDao
import world.inetumrealdolmen.mobiletrm.data.local.entities.asDbObject
import world.inetumrealdolmen.mobiletrm.data.local.entities.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType
import world.inetumrealdolmen.mobiletrm.data.remote.network.ApiService
import world.inetumrealdolmen.mobiletrm.data.util.AuthorizationHeaderInterceptor
import world.inetumrealdolmen.mobiletrm.ui.util.NotificationType
import world.inetumrealdolmen.mobiletrm.ui.util.displayNotification
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.UUID
import javax.inject.Inject

@HiltWorker
class TimeEntryUpdateSyncWorker
    @AssistedInject
    constructor(
        @Assisted val context: Context,
        @Assisted params: WorkerParameters,
        private val apiService: ApiService,
        private val dao: MobileTRMDao,
    ) : CoroutineWorker(context, params) {
        @Inject
        lateinit var credentialsManager: CredentialsManager

        @Inject
        lateinit var authorizationHeaderInterceptor: AuthorizationHeaderInterceptor

        override suspend fun doWork(): Result {
            val id = UUID.fromString(inputData.getString("id") ?: return Result.failure())
            val fromDb = dao.getRegistrationByLocalId(id)
            return try {
                authorizationHeaderInterceptor.token = credentialsManager.awaitCredentials().accessToken
                // Sync to cloud
                apiService.updateTimeRegistration(
                    TimeRegistrationUpdateDTO(
                        id = fromDb.remoteId,
                        description = fromDb.description ?: "",
                        startTime = fromDb.startTime,
                        endTime = fromDb.endTime,
                        assignedProjectId =
                            fromDb.assignedProject?.id ?: throw IllegalArgumentException(
                                "Project must be assigned to persist to API.",
                            ),
                        assignedTaskId = fromDb.assignedTask?.taskId,
                        tags = fromDb.asDomainObject().tags.map { it.asDto() },
                    ),
                )
                dao.delete(fromDb)

                // Update cache
                apiService.getTimeRegistrations()
                    .map { it.asDomainObject() }
                    .forEach {
                        dao.add(
                            // Fetch locally stored item & update or save as new item
                            dao.getRegistrationByRemoteId(it.remoteId)
                                ?: it.asDbObject(),
                        )
                    }

                context.displayNotification(
                    type = NotificationType.SYNC,
                    text = R.string.notifications_timeEntrySync_finished,
                )
                Result.success()
            } catch (e: Exception) {
                dao.add(
                    fromDb.copy(
                        syncingRetries = fromDb.syncingRetries++,
                        syncingError =
                            when (e) {
                                is SocketTimeoutException, is ConnectException -> ErrorType.SocketTimeout
                                is HttpException ->
                                    when (e.code()) {
                                        400 -> ErrorType.BadRequest
                                        404 -> ErrorType.NotFound
                                        500 -> ErrorType.Internal
                                        else -> ErrorType.Unknown(e.message)
                                    }
                                else -> ErrorType.Unknown(e.message)
                            },
                    ),
                )
                when (fromDb.syncingRetries < 3) {
                    true -> Result.retry()
                    false -> Result.failure()
                }
            }
        }
    }
