package com.github.godspeed010.weblib.feature_webview.presentation.webview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_webview.util.*
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

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
                Timber.d("EnteredUrl")

                state = state.copy(addressBarText = event.url)
            }
            is WebViewEvent.SubmittedUrl -> handleSubmittedUrl()
            is WebViewEvent.ToggleDarkMode -> {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && state.webViewSettings != null) {
                    val setting = if (state.isDarkModeEnabled) WebSettingsCompat.FORCE_DARK_OFF else WebSettingsCompat.FORCE_DARK_ON
                    WebSettingsCompat.setForceDark(state.webViewSettings!!, setting)
                    state = state.copy(isDarkModeEnabled = !state.isDarkModeEnabled)
                }
            }
            is WebViewEvent.ReloadClicked -> {
                Timber.d("ReloadClicked")
                state.webViewNavigator.reload()
            }
            is WebViewEvent.MoreOptionsToggled -> {
                state = state.copy(isMoreOptionsDropdownEnabled = !state.isMoreOptionsDropdownEnabled)
            }
            is WebViewEvent.NewPageVisited -> {
                Timber.d("NewPageVisited: ${event.url}")

                state = state.copy(addressBarText = event.url)
            }
            is WebViewEvent.WebPageScrolled -> updateToolbarOffsetHeight(event)
            is WebViewEvent.WebViewCreated -> {
                state = state.copy(webViewSettings = event.settings)
                state.webViewSettings?.javaScriptEnabled = true

                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && state.webViewSettings != null) {
                    WebSettingsCompat.setForceDark(state.webViewSettings!!, WebSettingsCompat.FORCE_DARK_ON)
                    state = state.copy(isDarkModeEnabled = !state.isDarkModeEnabled)
                }
            }
            is WebViewEvent.WebViewDisposed -> {
                state = state.copy(webViewSettings = null)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Timber.d("WebViewViewModel onStop")

        updateNovel()
    }

    /**
     * Handler functions (business logic)
     */

    private fun handleSubmittedUrl() {
        Timber.d("SubmittedUrl: ${state.addressBarText}")

        var addressBarText = state.addressBarText

        if (!addressBarText.isUrl()) {
            Timber.d("Entered text isn't url. Converting to Google search")
            addressBarText = addressBarText.asGoogleSearch()
        }
        else {
            addressBarText = addressBarText.makeHttpsIfNeeded()
        }

        Timber.d("Loading url: $addressBarText")
        state = state.copy(
            webViewState = WebViewState(WebContent.Url(addressBarText))
        )
    }

    private fun updateToolbarOffsetHeight(event: WebViewEvent.WebPageScrolled) {
        Timber.d("WebPageScrolled")

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