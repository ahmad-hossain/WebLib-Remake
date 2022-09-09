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
import com.github.godspeed010.weblib.core.model.Screen

@Composable
fun WebLibBottomAppBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    onClickHome: () -> Unit = {},
    onClickSearch: () -> Unit = {}
) {
    BottomAppBar(
        modifier = modifier,
        cutoutShape = CircleShape
    ) {
        BottomNavigation {
            BottomNavigationItem(
                selected = Screen.Home == currentScreen,
                onClick = onClickHome,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Home, contentDescription = "Home"
                    )
                }
            )
            BottomNavigationItem(
                selected = Screen.Search == currentScreen,
                onClick = onClickSearch,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Search, contentDescription = "Search"
                    )
                }
            )
        }
    }
}