package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.webkit.WebView

sealed class WebViewEvent {
    object ToggleDarkMode : WebViewEvent()
    data class SubmittedUrl(val url: String) : WebViewEvent()
    data class NewPageVisited(val url: String) : WebViewEvent()
    object ReloadClicked : WebViewEvent()
    object StopLoadingClicked : WebViewEvent()
    data class WebViewCreated(val webView: WebView, val isDeviceDarkModeEnabled: Boolean) : WebViewEvent()
    object WebViewDisposed : WebViewEvent()
    object BackButtonLongPressed : WebViewEvent()
    data class HistoryItemClicked(val listIndex: Int) : WebViewEvent()
    object BackButtonPressed : WebViewEvent()
}