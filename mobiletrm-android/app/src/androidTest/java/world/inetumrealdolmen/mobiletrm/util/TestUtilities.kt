package world.inetumrealdolmen.mobiletrm.util

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavController
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase.assertEquals

/**
 * Asserts that the current route in the NavController matches the expected route.
 * Takes the base route without the included arguments.
 *
 * @param expectedRouteName The expected name of the current route.
 */
fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route?.substringBefore("/"))
}

/**
 * Finds a Compose node identified by its string test tag using a StringRes ID.
 *
 * @param id The StringRes ID representing the string resource used as a test tag to identify the Compose node.
 * @return A SemanticsNodeInteraction for the identified Compose node.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithStringTestTag(
    @StringRes id: Int,
) = onNodeWithTag(activity.getString(id))

/**
 * Finds a Compose node identified by its string test tag using a StringRes ID and asserts if its text value is equal.
 *
 * @param id The StringRes ID representing the string resource used as a test tag to identify the Compose node.
 * @param value The StringRes ID representing the string resource the value is supposed to be.
 * @return A SemanticsNodeInteraction for the identified Compose node.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.assertTextEquals(
    @StringRes id: Int,
    @StringRes value: Int,
) = onNodeWithStringTestTag(id).assertTextEquals(activity.getString(value))

/**
 * Finds a Compose node identified by its string test tag using a StringRes ID and asserts if its text value is equal.
 *
 * @param id The StringRes ID representing the string resource used as a test tag to identify the Compose node.
 * @param value The StringRes ID representing the string resource the value is supposed to be.
 * @return A SemanticsNodeInteraction for the identified Compose node.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.assertTextEquals(
    @StringRes id: Int,
    @StringRes value: Int,
    n: Int,
) = onAllNodesWithStringTag(id)[n].assertTextEquals(activity.getString(value))

/**
 * Finds a Compose node identified by its string test tag using a StringRes ID  using a formatted string resource.
 * @param id The StringRes ID representing the string resource used as a test tag to identify the Compose node.
 * @param formatArg A string argument to be used as argument to specify which specific item
 * @return A SemanticsNodeInteraction for the identified Compose node.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithStringTestTag(
    @StringRes id: Int,
    formatArg: String,
): SemanticsNodeInteraction = onNodeWithTag(activity.getString(id, formatArg))

/**
 * Finds a node with content description from a string resource.
 *
 * @param id The resource ID of the content description.
 * @return A SemanticsNodeInteraction representing the node found.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithStringContentDescription(
    @StringRes id: Int,
): SemanticsNodeInteraction = onNodeWithContentDescription(activity.getString(id))

/**
 * Finds all nodes with a tag from a string resource.
 *
 * @param id The resource ID of the tag.
 * @return A SemanticsNodeInteractionCollection representing all nodes found.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onAllNodesWithStringTag(
    @StringRes id: Int,
): SemanticsNodeInteractionCollection = onAllNodesWithTag(activity.getString(id))
