package com.github.godspeed010.weblib.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.common.model.Screen
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
fun WebLibBottomAppBar_currentScreenIsHome() {
    WebLibTheme {
        WebLibBottomAppBar(
            currentScreen = Screen.Home
        )
    }
}
