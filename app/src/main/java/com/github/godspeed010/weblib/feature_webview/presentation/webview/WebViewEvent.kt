package com.github.godspeed010.weblib.feature_webview.presentation.webview

sealed class WebViewEvent {
    object ToggleDarkMode : WebViewEvent()
    data class EnteredUrl(val url: String) : WebViewEvent()
    object SubmittedUrl : WebViewEvent()
    data class NewPageVisited(val url: String) : WebViewEvent()
    object ReloadClicked : WebViewEvent()
    object MoreOptionsClicked : WebViewEvent()
}