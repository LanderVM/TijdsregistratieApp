package world.inetumrealdolmen.mobiletrm.data.repository.impl

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import world.inetumrealdolmen.mobiletrm.data.dto.ProjectDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationUpdateDTO
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainObjects
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainTaskObjects
import world.inetumrealdolmen.mobiletrm.data.dto.asDto
import world.inetumrealdolmen.mobiletrm.data.local.MobileTRMDao
import world.inetumrealdolmen.mobiletrm.data.local.entities.asDbObject
import world.inetumrealdolmen.mobiletrm.data.local.entities.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.model.Project
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetails
import world.inetumrealdolmen.mobiletrm.data.model.ProjectTasks
import world.inetumrealdolmen.mobiletrm.data.model.TimeEntry
import world.inetumrealdolmen.mobiletrm.data.model.TimeRegistration
import world.inetumrealdolmen.mobiletrm.data.remote.network.ApiService
import world.inetumrealdolmen.mobiletrm.data.remote.network.ConnectionState
import world.inetumrealdolmen.mobiletrm.data.remote.workers.FavoriteSyncWorker
import world.inetumrealdolmen.mobiletrm.data.remote.workers.ProjectsSyncWorker
import world.inetumrealdolmen.mobiletrm.data.remote.workers.TimeEntryCreateSyncWorker
import world.inetumrealdolmen.mobiletrm.data.remote.workers.TimeEntryDeleteSyncWorker
import world.inetumrealdolmen.mobiletrm.data.remote.workers.TimeEntryGetSyncWorker
import world.inetumrealdolmen.mobiletrm.data.remote.workers.TimeEntryUpdateSyncWorker
import world.inetumrealdolmen.mobiletrm.data.repository.MobileTRMRepository
import java.net.SocketException
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Implementation of [MobileTRMRepository] using the Quarkus API service.
 *
 * @param apiService The API service for fetching project data.
 * @param dao The DAO for fetching data from local storage.
 * @param connection The device's current network connection state.
 */
class QuarkusRepository
    @Inject
    constructor(
        private val apiService: ApiService,
        private val dao: MobileTRMDao,
        private val connection: LiveData<ConnectionState>,
        @ApplicationContext private val workerContext: Context,
    ) : MobileTRMRepository {
        override suspend fun getProjects(): Flow<List<Project>> {
            // Start background worker
            val request =
                OneTimeWorkRequestBuilder<ProjectsSyncWorker>().setConstraints(
                    Constraints(requiredNetworkType = NetworkType.CONNECTED),
                )
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        15L,
                        TimeUnit.SECONDS,
                    )
                    .build()
            WorkManager.getInstance(workerContext)
                .beginUniqueWork(
                    "syncProjects",
                    ExistingWorkPolicy.REPLACE,
                    request,
                )
                .enqueue()

            return dao.getAllProjects().map { projects -> projects.map { it.asDomainObject() } }
        }

        override suspend fun getProjectDetails(id: Long): ProjectDetails =
            withContext(Dispatchers.IO) {
                // Update cache if connection is available
                if (connection.value == ConnectionState.Available) {
                    // Cache project details
                    val projectDetails = apiService.getProjectDetails(id).asDomainObject()
                    dao.add(projectDetails.asDbObject())

                    // Cache time registrations
                    projectDetails.timeRegistration
                        .forEach {
                            dao.add(
                                // Fetch locally stored item & update or save as new item
                                dao.getRegistrationByRemoteId(it.remoteId)
                                    ?: it.asDbObject(),
                            )
                        }
                }

                val details = dao.getProjectDetails(id)
                details?.asDomainObject()
                    ?: ProjectDetails(
                        timeRegistration =
                            dao.getAllRegistrations()
                                .firstOrNull()
                                ?.filter { it.assignedProject?.id == id }
                                ?.map { it.asDomainObject() } ?: emptyList(),
                    )
            }

        override suspend fun getRunningProjectIds(): List<Long> =
            withContext(Dispatchers.IO) {
                dao.getAllProjects().first()
                    .filter {
                        if (it.workMinutesLeft != null) {
                            return@filter it.workMinutesLeft > 0
                        } else {
                            return@filter it.endDate?.toJavaLocalDate()?.isAfter(java.time.LocalDate.now()) == true
                        }
                    }.map { it.projectId }
            }

        override suspend fun getProjectTasks(): List<ProjectTasks> =
            withContext(Dispatchers.IO) {
                val finishedProjects = getRunningProjectIds()
                return@withContext try {
                    apiService.getProjectDetails().asDomainTaskObjects().filter { finishedProjects.contains(it.id) }
                } catch (e: SocketException) {
                    // Server is offline, use data from cache instead
                    dao.getAllProjects()
                        .firstOrNull()
                        ?.filter { finishedProjects.contains(it.projectId) }
                        ?.mapNotNull { dao.getProjectDetails(it.projectId) }
                        ?.map { ProjectTasks(it.project.projectId, it.project.name, it.details?.tasks ?: emptyList()) }
                        ?: emptyList()
                }
            }

        override suspend fun favoriteProject(
            id: Long,
            isFavorite: Boolean,
        ) {
            withContext(Dispatchers.IO) {
                val oldFavorite = dao.getFavoriteProject()

                if (connection.value == ConnectionState.Available) {
                    val result = apiService.setFavorite(id, ProjectDTO.UpdateFavorite(isFavorite))
                    dao.setFavorite(id, result)
                } else {
                    dao.setFavorite(id, isFavorite)
                    val request =
                        OneTimeWorkRequestBuilder<FavoriteSyncWorker>().setConstraints(
                            Constraints(requiredNetworkType = NetworkType.CONNECTED),
                        )
                            .setInputData(workDataOf("id" to id, "isFavorite" to isFavorite))
                            .build()

                    WorkManager.getInstance(workerContext)
                        .beginUniqueWork(
                            "favorite_$id",
                            ExistingWorkPolicy.REPLACE,
                            request,
                        )
                        .enqueue()
                }

                if (oldFavorite != null) {
                    dao.setFavorite(oldFavorite.projectId, false)
                }
            }
        }

        override suspend fun getFavoritedProject(): Project? =
            withContext(Dispatchers.IO) {
                dao.getFavoriteProject()?.asDomainObject()
            }

        override suspend fun createTimeRegistration(timeRegistration: List<TimeRegistration>) {
            val isConcept = timeRegistration.first().assignedProject == null
            withContext(Dispatchers.Main) {
                // Cache locally
                timeRegistration
                    .map { it.asDbObject() }
                    .forEach { dao.add(it) }

                // If it isn't a concept, queue the worker to sync with api
                if (!isConcept) {
                    val request =
                        OneTimeWorkRequestBuilder<TimeEntryCreateSyncWorker>().setConstraints(
                            Constraints(requiredNetworkType = NetworkType.CONNECTED),
                        )
                            .setInitialDelay(15L, TimeUnit.SECONDS)
                            .build()

                    WorkManager.getInstance(workerContext)
                        .beginUniqueWork(
                            "timeEntrySync",
                            ExistingWorkPolicy.REPLACE,
                            request,
                        )
                        .enqueue()
                }
            }
        }

        override suspend fun getTimeRegistrations(): Flow<List<TimeRegistration>> {
            // Update cache
            val request =
                OneTimeWorkRequestBuilder<TimeEntryGetSyncWorker>().setConstraints(
                    Constraints(requiredNetworkType = NetworkType.CONNECTED),
                )
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        15L,
                        TimeUnit.SECONDS,
                    )
                    .build()
            WorkManager.getInstance(workerContext)
                .beginUniqueWork(
                    "syncTimeEntries",
                    ExistingWorkPolicy.REPLACE,
                    request,
                )
                .enqueue()

            return dao.getAllRegistrations()
                .map { registrations -> registrations.map { it.asDomainObject() } }
        }

        override suspend fun getTimeEntries(date: LocalDate) =
            withContext(Dispatchers.IO) {
                if (connection.value == ConnectionState.Available) {
                    return@withContext apiService.getTimeEntries(date).asDomainObjects()
                }

                dao.getAllRegistrations().firstOrNull()
                    ?.filter { it.assignedProject != null && it.startTime.date == date }
                    ?.map { TimeEntry(it.remoteId ?: -1L, it.startTime.time, it.endTime.time) }
                    ?: emptyList()
            }

        override suspend fun getTimeRegistration(id: UUID) = dao.getRegistrationByLocalId(id).asDomainObject()

        override suspend fun deleteTimeRegistrationById(localId: UUID) {
            withContext(Dispatchers.IO) {
                val fromLocalDb = dao.getRegistrationByLocalId(localId)

                // Delete locally
                dao.delete(fromLocalDb)
                // If it's been persisted remotely, delete it from API
                if (fromLocalDb.remoteId != null) {
                    when (connection.value) {
                        ConnectionState.Available -> apiService.deleteTimeRegistrationById(fromLocalDb.remoteId)
                        ConnectionState.Unavailable, null -> {
                            val request =
                                OneTimeWorkRequestBuilder<TimeEntryDeleteSyncWorker>().setConstraints(
                                    Constraints(requiredNetworkType = NetworkType.CONNECTED),
                                )
                                    .setInputData(workDataOf("id" to fromLocalDb.remoteId))
                                    .build()

                            WorkManager.getInstance(workerContext)
                                .beginUniqueWork(
                                    "delete_${fromLocalDb.remoteId}",
                                    ExistingWorkPolicy.REPLACE,
                                    request,
                                )
                                .enqueue()
                        }
                    }
                }
            }
        }

        override suspend fun updateTimeRegistration(timeRegistration: TimeRegistration) {
            withContext(Dispatchers.IO) {
                // Concept is no longer a concept
                if (timeRegistration.remoteId != -1L) {
                    // Registration has been registered remotely
                    when (connection.value) {
                        ConnectionState.Available -> {
                            // Update remote registration
                            apiService.updateTimeRegistration(
                                TimeRegistrationUpdateDTO(
                                    id = timeRegistration.remoteId,
                                    description = timeRegistration.description ?: "",
                                    startTime = timeRegistration.startTime,
                                    endTime = timeRegistration.endTime,
                                    assignedProjectId =
                                        timeRegistration.assignedProject?.id ?: throw IllegalArgumentException(
                                            "Project must be assigned to persist to API.",
                                        ),
                                    assignedTaskId = timeRegistration.assignedTask?.taskId,
                                    tags = timeRegistration.tags.map { it.asDto() },
                                ),
                            )
                            dao.delete(timeRegistration.asDbObject())
                        }
                        ConnectionState.Unavailable, null -> {
                            val dbObject = timeRegistration.asDbObject()
                            dao.add(dbObject)

                            val request =
                                OneTimeWorkRequestBuilder<TimeEntryUpdateSyncWorker>().setConstraints(
                                    Constraints(requiredNetworkType = NetworkType.CONNECTED),
                                )
                                    .setInputData(workDataOf("id" to dbObject.localId.toString()))
                                    .build()

                            WorkManager.getInstance(workerContext)
                                .beginUniqueWork(
                                    "update_${dbObject.localId}",
                                    ExistingWorkPolicy.REPLACE,
                                    request,
                                )
                                .enqueue()
                        }
                    }
                    // Persist to API
                } else {
                    // Registration is still a concept
                    if (timeRegistration.assignedProject != null) {
                        // Shouldn't be a concept anymore and be registered remotely
                        createTimeRegistration(listOf(timeRegistration))
                    } else {
                        // Should remain a concept
                        dao.add(timeRegistration.asDbObject())
                    }
                }
            }
        }
    }
