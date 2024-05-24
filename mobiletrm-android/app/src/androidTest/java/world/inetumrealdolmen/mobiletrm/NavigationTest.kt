package world.inetumrealdolmen.mobiletrm

import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavigationRoutes
import world.inetumrealdolmen.mobiletrm.util.WorkManagerRule
import world.inetumrealdolmen.mobiletrm.util.assertCurrentRouteName
import world.inetumrealdolmen.mobiletrm.util.onAllNodesWithStringTag
import world.inetumrealdolmen.mobiletrm.util.onNodeWithStringTestTag

@HiltAndroidTest
class NavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val workerRule = WorkManagerRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 3)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

    private lateinit var navController: TestNavHostController

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
    }

    @Test
    fun navigate_homeToTimerToManual_toHome() {
        navigateTo(R.string.testTag_bottomNav_navigateAddTimeRegistration)
        navController.assertCurrentRouteName(NavigationRoutes.Timer.name)
        navigateTo(R.string.testTag_putTimeRegistration_modeSwitch_manual)
        navController.assertCurrentRouteName(NavigationRoutes.PutTimeRegistration.name)

        navigateTo(R.string.testTag_topNav_navigateBack)
        navController.assertCurrentRouteName(NavigationRoutes.Home.name)
    }

    @Test
    fun navigate_homeToProjectDetails() {
        composeTestRule.onAllNodesWithStringTag(R.string.testTag_projectCard)[0]
            .performClick()

        navController.assertCurrentRouteName(NavigationRoutes.ProjectDetailsOverview.name)
    }

    @Test
    fun navigate_timeRegistrationOverviewToHome_usingHomeButton() {
        navigateTo(R.string.testTag_bottomNav_navigateAddTimeRegistration)
        navigateTo(R.string.testTag_topNav_navigateBack)

        composeTestRule.onNodeWithStringTestTag(R.string.testTag_topNav_navigateBack)
            .assertDoesNotExist()
        navController.assertCurrentRouteName(NavigationRoutes.Home.name)
    }

    @Test
    fun navigate_timeRegistrationOverviewToHome_usingBackButton() {
        navigateTo(R.string.testTag_bottomNav_navigateAddTimeRegistration)
        navigateTo(R.string.testTag_topNav_navigateBack)

        composeTestRule.onNodeWithStringTestTag(R.string.testTag_topNav_navigateBack)
            .assertDoesNotExist()
        navController.assertCurrentRouteName(NavigationRoutes.Home.name)
    }

    @Test
    fun navigate_homeToAddTimeRegistrationAndBack_usingBackButton() {
        navigateTo(R.string.testTag_bottomNav_navigateAddTimeRegistration)
        navController.assertCurrentRouteName(NavigationRoutes.Timer.name)

        navigateTo(R.string.testTag_topNav_navigateBack)
        navController.assertCurrentRouteName(NavigationRoutes.Home.name)
    }
}
