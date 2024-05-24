package world.inetumrealdolmen.mobiletrm.data.remote.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.auth0.android.authentication.storage.CredentialsManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainObjects
import world.inetumrealdolmen.mobiletrm.data.local.MobileTRMDao
import world.inetumrealdolmen.mobiletrm.data.local.entities.asDbObject
import world.inetumrealdolmen.mobiletrm.data.remote.network.ApiService
import world.inetumrealdolmen.mobiletrm.data.util.AuthorizationHeaderInterceptor
import javax.inject.Inject

@HiltWorker
class ProjectsSyncWorker
    @AssistedInject
    constructor(
        @Assisted val context: Context,
        @Assisted params: WorkerParameters,
        private val apiService: ApiService,
        private val dao: MobileTRMDao,
        private val credentialsManager: CredentialsManager,
    ) : CoroutineWorker(context, params) {
        @Inject
        lateinit var authorizationHeaderInterceptor: AuthorizationHeaderInterceptor

        override suspend fun doWork(): Result {
            return try {
                authorizationHeaderInterceptor.token = credentialsManager.awaitCredentials().accessToken
                val fromDb = apiService.getProjects().asDomainObjects()
                dao.add(fromDb.map { it.asDbObject() })
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
