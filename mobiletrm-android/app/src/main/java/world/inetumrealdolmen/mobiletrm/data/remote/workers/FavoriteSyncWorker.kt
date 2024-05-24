package world.inetumrealdolmen.mobiletrm.data.remote.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.auth0.android.authentication.storage.CredentialsManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import world.inetumrealdolmen.mobiletrm.data.dto.ProjectDTO
import world.inetumrealdolmen.mobiletrm.data.local.MobileTRMDao
import world.inetumrealdolmen.mobiletrm.data.remote.network.ApiService
import world.inetumrealdolmen.mobiletrm.data.util.AuthorizationHeaderInterceptor
import javax.inject.Inject

@HiltWorker
class FavoriteSyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val apiService: ApiService,
        private val dao: MobileTRMDao,
    ) : CoroutineWorker(context, params) {
        @Inject
        lateinit var credentialsManager: CredentialsManager

        @Inject
        lateinit var authorizationHeaderInterceptor: AuthorizationHeaderInterceptor

        override suspend fun doWork(): Result =
            try {
                val id = inputData.getLong("id", -1)

                if (id == -1L) {
                    Result.failure()
                }

                val isFavorite = inputData.getBoolean("isFavorite", false)
                val oldFavorite = dao.getFavoriteProject()

                authorizationHeaderInterceptor.token = credentialsManager.awaitCredentials().accessToken

                val result = apiService.setFavorite(id, ProjectDTO.UpdateFavorite(isFavorite))
                dao.setFavorite(id, result)

                if (oldFavorite != null) {
                    dao.setFavorite(oldFavorite.projectId, false)
                }

                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
    }
