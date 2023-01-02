package com.github.godspeed010.weblib.core.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.core.model.Screen

@Composable
fun WebLibBottomAppBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    onClickHome: () -> Unit = {},
    onClickSearch: () -> Unit = {}
) {
    val navigationItems = arrayOf(
        NavItem(
            screen = Screen.Home,
            onClickAction = onClickHome,
            icon = Icons.Outlined.Home,
            iconContentDescriptionRes = R.string.cd_home,
        ),
        NavItem(
            screen = Screen.Search,
            onClickAction = onClickSearch,
            icon = Icons.Outlined.Search,
            iconContentDescriptionRes = R.string.cd_search,
        )
    )

    BottomAppBar(
        modifier = modifier,
        cutoutShape = CircleShape
    ) {
        BottomNavigation {
            navigationItems.forEach { navItem ->
                BottomNavigationItem(
                    selected = navItem.screen == currentScreen,
                    onClick = navItem.onClickAction,
                    icon = {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = stringResource(navItem.iconContentDescriptionRes)
                        )
                    }
                )
            }
        }
    }
}

data class NavItem(
    val screen: Screen,
    val onClickAction: () -> Unit,
    val icon: ImageVector,
    val iconContentDescriptionRes: Int,
)