package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.webkit.WebView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Density

sealed class WebViewEvent {
    object ToggleDarkMode : WebViewEvent()
    data class SubmittedUrl(val url: String) : WebViewEvent()
    data class NewPageVisited(val url: String) : WebViewEvent()
    data class WebPageScrolled(val localDensity: Density, val x: Int, val y: Int, val oldX: Int, val oldY: Int) : WebViewEvent()
    object ReloadClicked : WebViewEvent()
    object StopLoadingClicked : WebViewEvent()
    object MoreOptionsToggled : WebViewEvent()
    data class WebViewCreated(val webView: WebView, val isDeviceDarkModeEnabled: Boolean) : WebViewEvent()
    object WebViewDisposed : WebViewEvent()
    object UrlFocused : WebViewEvent()
    object BackButtonLongPressed : WebViewEvent()
    data class HistoryItemClicked(val listIndex: Int) : WebViewEvent()
    object BackButtonPressed : WebViewEvent()
}