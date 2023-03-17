package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.webkit.WebHistoryItem
import android.webkit.WebView
import androidx.compose.ui.text.input.TextFieldValue
import com.github.godspeed010.weblib.feature_webview.util.WebContent
import com.github.godspeed010.weblib.feature_webview.util.WebViewNavigator
import com.github.godspeed010.weblib.feature_webview.util.WebViewState

data class WebViewScreenState(
    val isWvDarkModeEnabled: Boolean = false,
    val webViewState: WebViewState = WebViewState(WebContent.Url("")),
    val webViewNavigator: WebViewNavigator,
    val toolbarOffsetHeightPx: Float = 0f,
    val isMoreOptionsDropdownEnabled: Boolean = false,
    val webView: WebView? = null,
    val shouldSelectEntireUrl: Boolean = false,
    val isLoadingDialogVisible: Boolean = false,
    val historyItems: List<WebHistoryItem> = listOf(),
)