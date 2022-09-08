package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_webview.util.WebContent
import com.github.godspeed010.weblib.feature_webview.util.WebViewNavigator
import com.github.godspeed010.weblib.feature_webview.util.WebViewState
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WebViewViewModel"
private val AppBarHeight = 56.dp

@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val repository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), DefaultLifecycleObserver {
    @OptIn(ExperimentalComposeUiApi::class)
    val novel: Novel = savedStateHandle.navArgs<WebViewScreenNavArgs>().novel

    var state by mutableStateOf(WebViewScreenState(webViewNavigator = WebViewNavigator(viewModelScope)))
        private set

    fun onEvent(event: WebViewEvent) {
        when(event) {
            is WebViewEvent.EnteredUrl -> {
                Log.d(TAG, "EnteredUrl")

                state = state.copy(addressBarText = event.url)
            }
            is WebViewEvent.SubmittedUrl -> {
                Log.d(TAG, "SubmittedUrl: ${state.addressBarText}")

                state = state.copy(
                    webViewState = WebViewState(WebContent.Url(state.addressBarText))
                )
            }
            is WebViewEvent.ToggleDarkMode -> TODO()
            is WebViewEvent.ReloadClicked -> {
                Log.d(TAG, "ReloadClicked")
                state.webViewNavigator.reload()
            }
            is WebViewEvent.MoreOptionsClicked -> TODO()
            is WebViewEvent.NewPageVisited -> {
                Log.d(TAG, "NewPageVisited: ${event.url}")

                state = state.copy(addressBarText = event.url)
            }
            is WebViewEvent.WebPageScrolled -> updateToolbarOffsetHeight(event)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d(TAG, "WebViewViewModel onStop")

        updateNovel()
    }

    private fun updateToolbarOffsetHeight(event: WebViewEvent.WebPageScrolled) {
        Log.d(TAG, "WebPageScrolled")

        val toolbarHeightPx = with(event.localDensity) { AppBarHeight.roundToPx().toFloat() }
        val deltaY = event.oldY - event.y
        val newOffset = state.toolbarOffsetHeightPx + deltaY
        state = state.copy(
            toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
        )
    }

    private fun updateNovel() {
        val currentUrl = state.webViewState.content.getCurrentUrl()
        currentUrl?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateNovel(novel.copy(url = it))
            }
        }
    }

    init {
        state = state.copy(
            webViewState = WebViewState(WebContent.Url(novel.url))
        )
    }
}