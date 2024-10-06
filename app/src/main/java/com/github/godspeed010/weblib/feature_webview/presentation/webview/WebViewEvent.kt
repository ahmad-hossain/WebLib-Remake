package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.webkit.WebView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Density

sealed class WebViewEvent {
    data object ToggleDarkMode : WebViewEvent()
    data class EnteredUrl(val url: TextFieldValue) : WebViewEvent()
    data object SubmittedUrl : WebViewEvent()
    data class NewPageVisited(val url: String) : WebViewEvent()
    data class WebPageScrolled(val localDensity: Density, val deltaY: Float) : WebViewEvent()
    data object ReloadClicked : WebViewEvent()
    data object StopLoadingClicked : WebViewEvent()
    data object MoreOptionsToggled : WebViewEvent()
    data class WebViewCreated(val webView: WebView, val isDeviceDarkModeEnabled: Boolean) : WebViewEvent()
    data object WebViewDisposed : WebViewEvent()
    data object UrlFocused : WebViewEvent()
    data object BackButtonLongPressed : WebViewEvent()
    data object HistoryDropdownDismissRequest : WebViewEvent()
    data class HistoryItemClicked(val listIndex: Int) : WebViewEvent()
}