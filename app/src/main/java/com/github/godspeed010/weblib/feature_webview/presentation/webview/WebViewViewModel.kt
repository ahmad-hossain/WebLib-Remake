package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

private const val TAG = "WebViewViewModel"

class WebViewViewModel() : ViewModel() {

    private val _webViewScreenState = mutableStateOf(WebViewState())
    val webViewScreenState: State<WebViewState> = _webViewScreenState

    fun onEvent(event: WebViewEvent) {
        when(event) {
            is WebViewEvent.EnteredUrl -> {
                Log.i(TAG, "EnteredUrl")

                _webViewScreenState.value = webViewScreenState.value.copy(
                    url = event.url
                )
            }
            is WebViewEvent.SubmitUrl -> TODO()
            is WebViewEvent.ToggleDarkMode -> TODO()
        }
    }
}