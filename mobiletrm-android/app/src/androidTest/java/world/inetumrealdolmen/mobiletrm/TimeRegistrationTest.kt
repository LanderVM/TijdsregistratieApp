package world.inetumrealdolmen.mobiletrm

import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.datetime.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes
import world.inetumrealdolmen.mobiletrm.ui.util.formatTime
import world.inetumrealdolmen.mobiletrm.util.WorkManagerRule
import world.inetumrealdolmen.mobiletrm.util.assertCurrentRouteName
import world.inetumrealdolmen.mobiletrm.util.assertTextEquals
import world.inetumrealdolmen.mobiletrm.util.onAllNodesWithStringTag
import world.inetumrealdolmen.mobiletrm.util.onNodeWithStringTestTag

@HiltAndroidTest
class TimeRegistrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val workerRule = WorkManagerRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 3)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

    private lateinit var navController: TestNavHostController

    private val correctStartTime = LocalTime(9, 30)
    private val correctEndTime = LocalTime(11, 30)

    private fun navigateTo(
        @StringRes id: Int,
    ) {
        composeTestRule
            .onNodeWithStringTestTag(id)
            .performClick()
    }

    @Before
    fun setup() {
        // use Hilt injection
        hiltRule.inject()

        // create navController and insert it into a new instantiation of the UI
        composeTestRule.activity.setContent {
            val context = composeTestRule.activity
            navController =
                TestNavHostController(context).apply {
                    navigatorProvider.addNavigator(ComposeNavigator())
                }
            Application(navController = navController)
        }
        getInstrumentation().waitForIdleSync()

        navigateTo(R.string.testTag_bottomNav_navigateAddTimeRegistration)
        navController.assertCurrentRouteName(NavigationRoutes.Timer.name)

        navigateTo(R.string.testTag_putTimeRegistration_modeSwitch_manual)
        navController.assertCurrentRouteName(NavigationRoutes.PutTimeRegistration.name)
    }

    @Test
    fun addTimeRegistration_happyFlow() {
        // Task is not enabled until project is chosen
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_taskDropdown_value)
            .assertIsNotEnabled()

        fillInCorrectDetails(correctStartTime, correctEndTime)

        // Assert form is filled in
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_projectDropdown_value)
            .assertTextEquals("Project X Fixed")
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_taskDropdown_value)
            .assertTextEquals("Task X1")
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_description)
            .assertTextEquals("Description")
        composeTestRule.onAllNodesWithStringTag(R.string.testTag_putTimeRegistration_timeEntry)[0]
            .assertTextEquals(formatTime(correctStartTime, multiLine = true))
        composeTestRule.onAllNodesWithStringTag(R.string.testTag_putTimeRegistration_timeEntry)[1]
            .assertTextEquals(formatTime(correctEndTime, multiLine = true))
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_timeEntry_addButton)
            .assertDoesNotExist()

        // Saves successfully and redirects to overview
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_save)
            .performClick()
        composeTestRule.waitUntil(5000) {
            NavigationRoutes.TimeRegistrationsOverview.name == navController.currentBackStackEntry?.destination?.route?.substringBefore("/")
        }
        navController.assertCurrentRouteName(NavigationRoutes.TimeRegistrationsOverview.name)
    }

    @Test
    fun addTimeRegistrationConcept_happyFlow() {
        fillInCorrectDetails(correctStartTime, correctEndTime, isConcept = true)

        // Assert form is filled in
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_taskDropdown_value)
            .assertIsNotEnabled()
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_description)
            .assertTextEquals("Description")
        composeTestRule.onAllNodesWithStringTag(R.string.testTag_putTimeRegistration_timeEntry)[0]
            .assertTextEquals(formatTime(correctStartTime, multiLine = true))
        composeTestRule.onAllNodesWithStringTag(R.string.testTag_putTimeRegistration_timeEntry)[1]
            .assertTextEquals(formatTime(correctEndTime, multiLine = true))
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_timeEntry_addButton)
            .assertDoesNotExist()

        // Saves successfully and redirects to overview
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_save)
            .performClick()
        composeTestRule.waitUntil(5000) {
            NavigationRoutes.TimeRegistrationsOverview.name == navController.currentBackStackEntry?.destination?.route?.substringBefore("/")
        }
        navController.assertCurrentRouteName(NavigationRoutes.TimeRegistrationsOverview.name)
    }

    @Test
    fun addTimeRegistration_duplicateTimeEntries() {
        selectProject()

        val startTime = LocalTime(9, 30)
        val endTime = LocalTime(10, 30)
        setTimeEntry(0, startTime)
        setTimeEntry(1, endTime)
        addTimeEntry()
        setTimeEntry(2, startTime)
        setTimeEntry(3, endTime)

        composeTestRule.assertTextEquals(
            R.string.testTag_putTimeRegistration_error,
            R.string.validations_overlappingTimeEntries,
            n = 0,
        )
    }

    @Test
    fun addTimeRegistration_duplicateTimeEntriesInDb() {
        selectProject()

        val startTime = LocalTime(2, 30)
        val endTime = LocalTime(5, 30)
        setTimeEntry(0, startTime)
        setTimeEntry(1, endTime)

        composeTestRule.assertTextEquals(
            R.string.testTag_putTimeRegistration_error,
            R.string.validations_overlappingTimeEntriesInDb,
            n = 0,
        )
    }

    @Test
    fun addTimeRegistration_startTimeAfterEndTime() {
        selectProject()

        val startTime = LocalTime(9, 30)
        val endTime = LocalTime(10, 30)
        setTimeEntry(0, endTime)
        setTimeEntry(1, startTime)

        composeTestRule.assertTextEquals(
            R.string.testTag_putTimeRegistration_error,
            R.string.validations_startTimeAfterEndTime,
            n = 0,
        )
    }

    @Test
    fun addTimeRegistration_startTimeIsEndTime() {
        selectProject()

        val startTime = LocalTime(9, 30)
        setTimeEntry(0, startTime)
        setTimeEntry(1, startTime)

        composeTestRule.assertTextEquals(
            R.string.testTag_putTimeRegistration_error,
            R.string.validations_startTimeIsEndTime,
            n = 0,
        )
    }

    @Test
    fun addTimeRegistration_overlappingTimeEntries() {
        selectProject()

        val startTime = LocalTime(8, 30)
        val startTimeInBetween = LocalTime(9, 0)
        val endTime = LocalTime(10, 30)
        val endTime2 = LocalTime(11, 0)
        setTimeEntry(0, startTime)
        setTimeEntry(1, endTime)
        addTimeEntry()
        setTimeEntry(2, startTimeInBetween)
        setTimeEntry(3, endTime2)

        composeTestRule.assertTextEquals(
            R.string.testTag_putTimeRegistration_error,
            R.string.validations_overlappingTimeEntries,
            n = 0,
        )
    }

    private fun selectProject() {
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_projectDropdown)
            .performClick()
        composeTestRule.onNodeWithStringTestTag(
            R.string.testTag_putTimeRegistration_projectDropdown_option,
            "Project X Fixed",
        )
            .performClick()
    }

    private fun fillInCorrectDetails(
        startTime: LocalTime,
        endTime: LocalTime,
        isConcept: Boolean = false,
    ) {
        if (!isConcept) {
            // Select project
            selectProject()

            // Select task
            composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_taskDropdown)
                .performClick()
            composeTestRule.onNodeWithStringTestTag(
                R.string.testTag_putTimeRegistration_taskDropdown_option,
                "Task X1",
            )
                .performClick()
        }

        // Set description
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_description)
            .performTextInput("Description")

        // Set time entries

        setTimeEntry(0, startTime)
        setTimeEntry(1, endTime)
        addTimeEntry()
        addTimeEntry()
    }

    private fun setTimeEntry(
        n: Int,
        time: LocalTime,
    ) {
        composeTestRule.onAllNodesWithStringTag(R.string.testTag_putTimeRegistration_timeEntry)[n]
            .performClick()
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_timeEntry_dialog_keyboardMode)
            .performClick()
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_timeEntry_dialog_timeField)
            .onChildren()[0]
            .performTextReplacement("${time.hour}")
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_timeEntry_dialog_timeField)
            .onChildren()[4]
            .performTextReplacement("${time.minute}")
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_timeEntry_dialog_confirm)
            .performClick()
    }

    private fun addTimeEntry() {
        composeTestRule.onNodeWithStringTestTag(R.string.testTag_putTimeRegistration_timeEntry_addButton)
            .performClick()
    }
}
