package com.github.godspeed010.weblib.feature_webview.util

import android.webkit.WebView

class CustomWebViewClient(
    private val onNewPageVisited: (String) -> Unit
) : AccompanistWebViewClient() {
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)

        url?.let { onNewPageVisited(it) }
    }
}