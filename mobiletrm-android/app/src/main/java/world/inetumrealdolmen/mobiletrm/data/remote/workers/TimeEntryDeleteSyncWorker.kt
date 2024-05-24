package world.inetumrealdolmen.mobiletrm.data.remote.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.auth0.android.authentication.storage.CredentialsManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.local.MobileTRMDao
import world.inetumrealdolmen.mobiletrm.data.local.entities.asDbObject
import world.inetumrealdolmen.mobiletrm.data.local.entities.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.remote.network.ApiService
import world.inetumrealdolmen.mobiletrm.data.util.AuthorizationHeaderInterceptor
import world.inetumrealdolmen.mobiletrm.ui.util.NotificationType
import world.inetumrealdolmen.mobiletrm.ui.util.displayNotification
import javax.inject.Inject

@HiltWorker
class TimeEntryDeleteSyncWorker
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
            val id = inputData.getLong("id", -1)

            if (id == -1L) return Result.failure()

            return try {
                authorizationHeaderInterceptor.token = credentialsManager.awaitCredentials().accessToken
                // Sync to cloud
                apiService.deleteTimeRegistrationById(id)

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
                Log.e("TimeEntryDeleteSyncWorker", "Deleting failed")
                Result.failure()
            }
        }
    }
