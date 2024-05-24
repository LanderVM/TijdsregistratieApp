package world.inetumrealdolmen.mobiletrm.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import world.inetumrealdolmen.mobiletrm.data.dto.CustomerDTO
import world.inetumrealdolmen.mobiletrm.data.dto.ProjectDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TaskDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeEntryDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationCreateDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationDTO
import world.inetumrealdolmen.mobiletrm.data.dto.TimeRegistrationUpdateDTO
import world.inetumrealdolmen.mobiletrm.data.dto.asDomainObject
import world.inetumrealdolmen.mobiletrm.data.local.LocalDatabase
import world.inetumrealdolmen.mobiletrm.data.local.MobileTRMDao
import world.inetumrealdolmen.mobiletrm.data.local.PreferencesDao
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbProject
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbProjectDetails
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimeRegistration
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimerEntry
import world.inetumrealdolmen.mobiletrm.data.local.entities.FilterPreferences
import world.inetumrealdolmen.mobiletrm.data.local.entities.ProjectWithDetails
import world.inetumrealdolmen.mobiletrm.data.local.entities.SnackbarPreferences
import world.inetumrealdolmen.mobiletrm.data.remote.network.ApiService
import world.inetumrealdolmen.mobiletrm.data.remote.network.ConnectionState
import world.inetumrealdolmen.mobiletrm.data.util.AppModule
import world.inetumrealdolmen.mobiletrm.data.util.LocalStorageModule
import world.inetumrealdolmen.mobiletrm.ui.util.now
import java.util.UUID
import javax.inject.Singleton
import kotlin.random.Random

/**
 * AppModule mock for Hilt to inject in instrumented tests.
 */
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class, LocalStorageModule::class],
)
@Module
object FakeAppModule {
    private val listProjects =
        listOf(
            ProjectDTO.Index(
                id = 1,
                name = "Project X Fixed",
                endDate = LocalDate.now().plus(30, DateTimeUnit.DAY),
                workMinutesLeft = null,
                isFavorite = false,
                whenFavorited = Clock.System.now().toLocalDateTime(TimeZone.UTC),
            ),
            ProjectDTO.Index(
                id = 2,
                name = "Project Y Fixed without Tasks",
                endDate = LocalDate.now().plus(90, DateTimeUnit.DAY),
                workMinutesLeft = null,
                isFavorite = false,
                whenFavorited = Clock.System.now().toLocalDateTime(TimeZone.UTC),
            ),
            ProjectDTO.Index(
                id = 3,
                name = "Project Z Variable",
                endDate = null,
                workMinutesLeft = 600,
                isFavorite = false,
                whenFavorited = Clock.System.now().toLocalDateTime(TimeZone.UTC),
            ),
        )

    val listTimeRegistration =
        mutableListOf(
            TimeRegistrationDTO(
                id = Random.nextLong(),
                description = "A description",
                startTime =
                    LocalTime(13, 0).atDate(
                        LocalDate.now().minus(10, DateTimeUnit.DAY),
                    ),
                endTime =
                    LocalTime(15, 0).atDate(
                        LocalDate.now().minus(10, DateTimeUnit.DAY),
                    ),
                assignedTask = null,
                assignedProject =
                    ProjectDTO.IndexShort(
                        listProjects[0].id,
                        listProjects[0].name,
                    ),
                tags = listOf(),
            ),
            TimeRegistrationDTO(
                id = Random.nextLong(),
                description = "A description",
                startTime =
                    LocalTime(2, 0).atDate(
                        LocalDate.now(),
                    ),
                endTime =
                    LocalTime(7, 0).atDate(
                        LocalDate.now(),
                    ),
                assignedTask = null,
                assignedProject =
                    ProjectDTO.IndexShort(
                        listProjects[0].id,
                        listProjects[0].name,
                    ),
                tags = listOf(),
            ),
        )

    /**
     * Provides a mock for Hilt to inject into the MobileTRMRepository
     */
    @Singleton
    @Provides
    fun providesApiService(): ApiService =
        object : ApiService {
            override suspend fun getProjects(): List<ProjectDTO.Index> = listProjects

            override suspend fun getProjectDetails(id: Long): ProjectDTO.Details {
                val result = listProjects.first { it.id == id }
                return ProjectDTO.Details(
                    id = result.id,
                    name = result.name,
                    description = "____",
                    customer = CustomerDTO(5, "____", "____"),
                    startDate = LocalDate.now().minus(90, DateTimeUnit.DAY),
                    endDate = result.endDate,
                    workMinutesLeft = result.workMinutesLeft,
                    tasks = listOf(),
                    timeRegistration = listOf(),
                )
            }

            override suspend fun getProjectDetails() =
                listOf(
                    ProjectDTO.Tasks(
                        id = 1,
                        name = "Project X Fixed",
                        tasks =
                            listOf(
                                TaskDTO(1, "Task X1"),
                                TaskDTO(2, "Task X2"),
                            ),
                    ),
                    ProjectDTO.Tasks(
                        id = 2,
                        name = "Project Y Variable",
                        tasks =
                            listOf(
                                TaskDTO(3, "Task Y3"),
                            ),
                    ),
                    ProjectDTO.Tasks(
                        id = 3,
                        name = "Project Z Fixed without Tasks",
                        tasks = listOf(),
                    ),
                )

            override suspend fun setFavorite(
                id: Long,
                isFavorite: ProjectDTO.UpdateFavorite,
            ) = true

            override suspend fun createTimeRegistration(timeRegistrationDTO: List<TimeRegistrationCreateDTO>) {}

            override suspend fun getTimeRegistrations() = listTimeRegistration

            override suspend fun getTimeRegistration(id: Long) =
                getTimeRegistrations().first {
                    it.id == id
                }

            override suspend fun deleteTimeRegistrationById(id: Long) {
                listTimeRegistration.first { it.id == id }
            }

            override suspend fun updateTimeRegistration(timeRegistrationDTO: TimeRegistrationUpdateDTO) {
                listTimeRegistration.first { it.id == timeRegistrationDTO.id }
            }

            override suspend fun getTimeEntries(date: LocalDate): List<TimeEntryDTO> =
                listOf(
                    TimeEntryDTO(
                        id = -1,
                        startTime = LocalTime(3, 30),
                        endTime = LocalTime(4, 30),
                    ),
                )
        }

    @Singleton
    @Provides
    fun providesDao() =
        object : MobileTRMDao {
            override fun getAllRegistrations(): Flow<List<DbTimeRegistration>> =
                flowOf(
                    listTimeRegistration.map {
                        DbTimeRegistration(
                            localId = UUID.randomUUID(),
                            remoteId = it.id,
                            description = it.description,
                            startTime = it.startTime,
                            endTime = it.endTime,
                            assignedTask = it.assignedTask?.asDomainObject(),
                            assignedProject = it.assignedProject,
                            tags = it.tags.map { tag -> tag.name },
                        )
                    },
                )

            override fun getUnsyncedRegistrations(): List<DbTimeRegistration> {
                TODO("Not yet implemented")
            }

            override fun getAllProjects(): Flow<List<DbProject>> =
                flowOf(
                    listProjects.map {
                        DbProject(
                            projectId = it.id,
                            name = it.name,
                            endDate = it.endDate,
                            workMinutesLeft = it.workMinutesLeft,
                            isFavorite = it.isFavorite,
                        )
                    },
                )

            override fun getProjectDetails(projectId: Long): ProjectWithDetails? {
                TODO("Not yet implemented")
            }

            override suspend fun add(item: DbTimeRegistration) {
            }

            override suspend fun add(item: List<DbProject>) {
            }

            override suspend fun add(item: DbProjectDetails) {
            }

            override suspend fun delete(item: DbTimeRegistration) {
            }

            override suspend fun delete(id: UUID) {
            }

            override suspend fun getRegistrationByLocalId(id: UUID): DbTimeRegistration {
                TODO("Not yet implemented")
            }

            override suspend fun getRegistrationByRemoteId(id: Long): DbTimeRegistration? {
                TODO("Not yet implemented")
            }

            override fun setFavorite(
                id: Long,
                result: Boolean,
            ) {
            }

            override fun getFavoriteProject(): DbProject? = null
        }

    @Singleton
    @Provides
    fun providesPreferencesDao() =
        object : PreferencesDao {
            override suspend fun getFilterPreferences(): FilterPreferences = FilterPreferences()

            override suspend fun updateFilterPreferences(filterPreferences: FilterPreferences) {
            }

            override suspend fun getSnackbarPreferences(): SnackbarPreferences = SnackbarPreferences()

            override suspend fun updateSnackbarPreferences(snackbarPreferences: SnackbarPreferences) {
            }

            override fun getTimerEntries(): Flow<List<DbTimerEntry>> = flowOf()

            override suspend fun addTimerEntry(entry: DbTimerEntry) {
            }

            override suspend fun deleteTimerEntries() {
            }
        }

    @Singleton
    @Provides
    fun provideConnectivityState(): LiveData<ConnectionState> = MutableLiveData()

    @Singleton
    @Provides
    fun providesDb(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        "mobiletrm-test-db",
    )
        .enableMultiInstanceInvalidation()
        .fallbackToDestructiveMigration()
        .build()
}
