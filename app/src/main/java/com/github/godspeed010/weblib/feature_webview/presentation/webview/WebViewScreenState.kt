package com.github.godspeed010.weblib.feature_webview.presentation.webview

import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewState

data class WebViewScreenState(
    val addressBarUrl: String = "",
    val isDarkModeEnabled: Boolean = false,
    val webViewState: WebViewState = WebViewState(WebContent.Url(""))
)