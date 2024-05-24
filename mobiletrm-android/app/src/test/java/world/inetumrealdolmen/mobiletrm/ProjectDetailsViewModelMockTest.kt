package world.inetumrealdolmen.mobiletrm

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.model.ProjectDetails
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.screen.projectdetails.ProjectDetailsViewModel

/**
 * This class tests the ProjectDetailsViewModel.
 *
 * @property quarkusRepositoryMock The mock repository used for testing.
 * @property viewModel The ViewModel being tested.
 * @property testDispatcher The dispatcher used for testing.
 * @property projectsMockData The mock data used for testing.
 */
@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ProjectDetailsViewModelMockTest {
    @Mock
    private lateinit var quarkusRepositoryMock: QuarkusRepository

    private lateinit var viewModel: ProjectDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    /**
     * The mock data used for testing.
     */
    private val projectsMockData =
        listOf(
            ProjectDetails(
                id = 998,
                name = "Project 998",
                endDate = null,
                workMinutesLeft = 85600,
            ),
            ProjectDetails(
                id = 999,
                name = "Project 999",
                endDate = LocalDate(2020, 1, 8),
                workMinutesLeft = null,
            ),
            ProjectDetails(
                id = -2,
                name = "12",
                endDate = LocalDate(-1020, 1, 8),
                workMinutesLeft = -584,
            ),
        )

    /**
     * Trains the mock repository with predefined responses.
     */
    private fun trainMock() {
        val testScope = CoroutineScope(Dispatchers.Default)
        testScope.launch {
            Mockito.lenient().`when`(quarkusRepositoryMock.getProjectDetails(998)).thenReturn(
                projectsMockData[0],
            )
            Mockito.lenient().`when`(quarkusRepositoryMock.getProjectDetails(999)).thenReturn(
                projectsMockData[1],
            )
            Mockito.lenient().`when`(quarkusRepositoryMock.getProjectDetails(-2)).thenReturn(
                projectsMockData[2],
            )
        }
    }

    /**
     * Sets up the ViewModel and mock responses before each test.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        trainMock()

        create_viewmodel_loading_and_success()
    }

    /**
     * Resets the main dispatcher after each test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests the creation of the ViewModel and its initial loading state.
     */
    @Test
    fun create_viewmodel_loading_and_success() =
        runTest {
            viewModel = ProjectDetailsViewModel(quarkusRepositoryMock)
            assertEquals(ApiState.Loading, viewModel.apiState)
        }

    /**
     * Tests the retrieval of a project with a correct ID.
     */
    @Test
    fun get_project_correct_id_success() =
        runTest {
            val projectId = 998L

            viewModel.fetchProjectDetails(projectId)
            advanceUntilIdle()

            assertEquals(ApiState.Success, viewModel.apiState)
            assertEquals(projectsMockData[0], viewModel.uiState.value.details)
        }
}
