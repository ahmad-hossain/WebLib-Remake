package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    private val libraryRepository: LibraryRepository,
    state: SavedStateHandle
) : ViewModel(), DefaultLifecycleObserver {
    @OptIn(ExperimentalComposeUiApi::class)
    val novel: Novel = state.navArgs<WebViewScreenNavArgs>().novel

    private val _webViewScreenState = mutableStateOf(WebViewScreenState(webViewNavigator = WebViewNavigator(viewModelScope)))
    val webViewScreenState: State<WebViewScreenState> = _webViewScreenState

    fun onEvent(event: WebViewEvent) {
        when(event) {
            is WebViewEvent.EnteredUrl -> {
                Log.d(TAG, "EnteredUrl")

                _webViewScreenState.value = webViewScreenState.value.copy(addressBarText = event.url)
            }
            is WebViewEvent.SubmittedUrl -> {
                Log.d(TAG, "SubmittedUrl: ${webViewScreenState.value.addressBarText}")

                _webViewScreenState.value = webViewScreenState.value.copy(
                    webViewState = WebViewState(WebContent.Url(webViewScreenState.value.addressBarText))
                )
            }
            is WebViewEvent.ToggleDarkMode -> TODO()
            is WebViewEvent.ReloadClicked -> {
                Log.d(TAG, "ReloadClicked")
                _webViewScreenState.value.webViewNavigator.reload()
            }
            is WebViewEvent.MoreOptionsClicked -> TODO()
            is WebViewEvent.NewPageVisited -> {
                Log.d(TAG, "NewPageVisited: ${event.url}")

                _webViewScreenState.value = webViewScreenState.value.copy(addressBarText = event.url)
            }
            is WebViewEvent.WebPageScrolled -> {
                Log.d(TAG, "WebPageScrolled")

                //todo move into use case?
                val toolbarHeightPx = with(event.localDensity) { AppBarHeight.roundToPx().toFloat() }
                val deltaY = event.oldY - event.y
                val newOffset = webViewScreenState.value.toolbarOffsetHeightPx + deltaY
                _webViewScreenState.value = webViewScreenState.value.copy(
                    toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
                )
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d(TAG, "WebViewViewModel onStop")

        updateNovel()
    }

    private fun updateNovel() {
        val currentUrl = webViewScreenState.value.webViewState.content.getCurrentUrl()
        currentUrl?.let {
            viewModelScope.launch(Dispatchers.IO) {
                libraryRepository.updateNovel(novel.copy(url = it))
            }
        }
    }

    init {
        _webViewScreenState.value = webViewScreenState.value.copy(
            webViewState = WebViewState(WebContent.Url(novel.url))
        )
    }
}