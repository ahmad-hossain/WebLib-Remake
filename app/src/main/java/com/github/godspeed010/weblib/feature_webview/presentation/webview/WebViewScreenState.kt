package com.github.godspeed010.weblib.feature_webview.presentation.webview

import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState

data class WebViewScreenState(
    val addressBarText: String = "",
    val isDarkModeEnabled: Boolean = false,
    val webViewState: WebViewState = WebViewState(WebContent.Url("")),
    val webViewNavigator: WebViewNavigator
)