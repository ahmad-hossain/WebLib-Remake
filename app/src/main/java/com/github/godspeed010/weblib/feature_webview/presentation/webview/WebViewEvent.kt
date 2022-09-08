package com.github.godspeed010.weblib.feature_webview.presentation.webview

import androidx.compose.ui.unit.Density

sealed class WebViewEvent {
    object ToggleDarkMode : WebViewEvent()
    data class EnteredUrl(val url: String) : WebViewEvent()
    object SubmittedUrl : WebViewEvent()
    data class NewPageVisited(val url: String) : WebViewEvent()
    data class WebPageScrolled(val localDensity: Density, val x: Int, val y: Int, val oldX: Int, val oldY: Int) : WebViewEvent()
    object ReloadClicked : WebViewEvent()
    object MoreOptionsClicked : WebViewEvent()
}