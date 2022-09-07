package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.navArgs
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState

private const val TAG = "WebViewViewModel"

class WebViewViewModel(
    state: SavedStateHandle
) : ViewModel() {
    @OptIn(ExperimentalComposeUiApi::class)
    val novel: Novel = state.navArgs<WebViewScreenNavArgs>().novel

    private val _webViewScreenState = mutableStateOf(WebViewScreenState(webViewNavigator = WebViewNavigator(viewModelScope)))
    val webViewScreenState: State<WebViewScreenState> = _webViewScreenState

    fun onEvent(event: WebViewEvent) {
        when(event) {
            is WebViewEvent.EnteredUrl -> {
                Log.i(TAG, "EnteredUrl")

                _webViewScreenState.value = webViewScreenState.value.copy(
                    addressBarUrl = event.url
                )
            }
            is WebViewEvent.SubmitUrl -> {
                //todo update webViewState with new URL
            }
            is WebViewEvent.ToggleDarkMode -> TODO()
            is WebViewEvent.ReloadClicked -> {
                _webViewScreenState.value.webViewNavigator.reload()
            }
            is WebViewEvent.MoreOptionsClicked -> TODO()
        }
    }

    init {
        _webViewScreenState.value = webViewScreenState.value.copy(
            webViewState = WebViewState(WebContent.Url(novel.url))
        )
    }
}