package com.github.godspeed010.weblib.feature_webview.presentation.webview

import com.github.godspeed010.weblib.feature_webview.util.WebContent
import com.github.godspeed010.weblib.feature_webview.util.WebViewNavigator
import com.github.godspeed010.weblib.feature_webview.util.WebViewState

data class WebViewScreenState(
    val addressBarText: String = "",
    val isDarkModeEnabled: Boolean = false,
    val webViewState: WebViewState = WebViewState(WebContent.Url("")),
    val webViewNavigator: WebViewNavigator,
    val toolbarOffsetHeightPx: Float = 0f,
)