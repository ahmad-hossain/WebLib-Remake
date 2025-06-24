package com.github.godspeed010.weblib.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.common.model.Screen

data class NavItem(
    val screen: Screen,
    val onClickAction: () -> Unit,
    val icon: ImageVector,
    val iconContentDescriptionRes: Int,
)

@Composable
fun WebLibBottomAppBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    onClickHome: () -> Unit = {},
    onClickSettings: () -> Unit = {}
) {
    /** List of all BottomAppBar screens in the same order shown in [Screen] */
    val navigationItems = arrayOf(
        NavItem(
            screen = Screen.Home,
            onClickAction = onClickHome,
            icon = Icons.Outlined.Home,
            iconContentDescriptionRes = R.string.cd_home,
        ),
        NavItem(
            screen = Screen.Settings,
            onClickAction = onClickSettings,
            icon = Icons.Outlined.Settings,
            iconContentDescriptionRes = R.string.cd_settings,
        )
    )

    TabRow(
        modifier = modifier,
        selectedTabIndex = currentScreen.ordinal
    ) {
        navigationItems.forEachIndexed { index, navItem ->
            Tab(
                selected = currentScreen.ordinal == index,
                onClick = navItem.onClickAction,
                icon = {
                    Icon(
                        navItem.icon,
                        contentDescription = stringResource(id = navItem.iconContentDescriptionRes)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewWebLibBottomAppBar() {
    WebLibBottomAppBar(
        currentScreen = Screen.Home
    )
}