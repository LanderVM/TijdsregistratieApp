package world.inetumrealdolmen.mobiletrm.ui.common.navigation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.auth0.android.result.UserProfile
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.ui.util.restart

/**
 * Composable that represents a navigation top bar with title, navigation icon (either back button or hamburger menu to open the [NavDrawer]), and a profile icon.
 *
 * @param title The title text to display in the navigation bar.
 * @param canNavigateBack Whether the navigation icon should be an arrow back button.
 * @param navigateUp Callback for navigating back.
 * @param openDrawer Callback for opening the navigation drawer.
 * @param modifier Modifier for custom styling.
 * @param userInfo The user's info to display (optional)
 * @param viewModel ViewModel that holds the state and business logic for this screen. Injected by Hilt by default.
 * @param content The icons to display to the right of the title and before the user's profile picture (if displayed).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavTopBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    userInfo: UserProfile? = null,
    viewModel: NavTopBarViewModel = hiltViewModel(),
    content: @Composable () -> Unit = {},
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun logOut() {
        viewModel.logOut()
        context.restart()
    }

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    modifier = Modifier.testTag(stringResource(id = R.string.testTag_topNav_navigateBack)),
                    onClick = navigateUp,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.contentDescription_navigateBack),
                    )
                }
            } else {
                IconButton(
                    modifier = Modifier.testTag(stringResource(id = R.string.testTag_navigateHamburger)),
                    onClick = openDrawer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(id = R.string.contentDescription_navigateHamburger),
                    )
                }
            }
        },
        actions = {
            content()
            if (userInfo != null) {
                ReusableDropDownMenu(
                    expandedState = expandedDropdown,
                    setExpanded = { expandedDropdown = it },
                    button = {
                        IconButton(
                            modifier = Modifier.testTag(stringResource(id = R.string.testTag_topNav_profilePicture)),
                            onClick = { expandedDropdown = true },
                        ) {
                            AsyncImage(
                                model = userInfo.pictureURL,
                                error = painterResource(id = R.drawable.account),
                                fallback = painterResource(id = R.drawable.account),
                                contentDescription = stringResource(id = R.string.contentDescription_navigateProfile),
                            )
                        }
                    },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.auth_button_logout)) },
                        onClick = {
                            expandedDropdown = false
                            logOut()
                        },
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.logout),
                                contentDescription = stringResource(R.string.contentDescription_logOutButton),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        },
                        modifier = Modifier.testTag(stringResource(R.string.testTag_topNav_profilePicture_logOut)),
                    )
                }
            }
        },
    )
}

// TODO reuse this in infocard
@Composable
fun ReusableDropDownMenu(
    expandedState: Boolean,
    setExpanded: (expand: Boolean) -> Unit,
    button: (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    menuItems: (@Composable () -> Unit),
) {
    Box(
        modifier =
            modifier
                .wrapContentSize(Alignment.TopStart),
    ) {
        button()
        DropdownMenu(
            expanded = expandedState,
            onDismissRequest = { setExpanded(!expandedState) },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 8.dp,
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .width(IntrinsicSize.Max)
                    .padding(horizontal = 12.dp),
        ) {
            menuItems()
        }
    }
}
