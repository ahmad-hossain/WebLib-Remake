package com.github.godspeed010.weblib.feature_webview.presentation.webview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
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
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when(event) {
            is WebViewEvent.EnteredUrl -> {
                val addressBarTextStr = state.addressBarText.text

                state = if (state.shouldSelectEntireUrl) {
                    state.copy(
                        addressBarText = TextFieldValue(
                            text = addressBarTextStr,
                            selection = TextRange(0, addressBarTextStr.length)
                        ),
                        shouldSelectEntireUrl = false
                    )
                } else {
                    state.copy(addressBarText = event.url)
                }
            }
            is WebViewEvent.SubmittedUrl -> {
                var addressBarText = state.addressBarText.text

                if (!addressBarText.isUrl()) {
                    Timber.d("Entered text isn't url. Converting to Google search")
                    addressBarText = addressBarText.asGoogleSearch()
                } else {
                    addressBarText = addressBarText.makeHttpsIfNeeded()
                }

                Timber.d("Loading url: $addressBarText")
                state = state.copy(
                    webViewState = WebViewState(WebContent.Url(addressBarText))
                )
            }
            is WebViewEvent.ToggleDarkMode -> {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && state.webViewSettings != null) {
                    val setting = if (state.isDarkModeEnabled) WebSettingsCompat.FORCE_DARK_OFF else WebSettingsCompat.FORCE_DARK_ON
                    WebSettingsCompat.setForceDark(state.webViewSettings!!, setting)
                    state = state.copy(isDarkModeEnabled = !state.isDarkModeEnabled)
                }
            }
            is WebViewEvent.ReloadClicked -> {
                state.webViewNavigator.reload()
            }
            is WebViewEvent.StopLoadingClicked -> {
                state.webViewNavigator.stopLoading()
            }
            is WebViewEvent.MoreOptionsToggled -> {
                state = state.copy(isMoreOptionsDropdownEnabled = !state.isMoreOptionsDropdownEnabled)
            }
            is WebViewEvent.NewPageVisited -> {
                state = state.copy(addressBarText = TextFieldValue(event.url))
            }
            is WebViewEvent.WebPageScrolled -> {
                val toolbarHeightPx = with(event.localDensity) { AppBarHeight.roundToPx().toFloat() }
                val deltaY = event.oldY - event.y
                val newOffset = state.toolbarOffsetHeightPx + deltaY
                Timber.d("Updated toolbar offset: toolbarOffsetHeightPx=$newOffset")

                state = state.copy(
                    toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
                )
            }
            is WebViewEvent.WebViewCreated -> {
                state = state.copy(webViewSettings = event.settings)
                state.webViewSettings?.javaScriptEnabled = true

                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && state.webViewSettings != null) {
                    WebSettingsCompat.setForceDark(state.webViewSettings!!, WebSettingsCompat.FORCE_DARK_ON)
                    state = state.copy(isDarkModeEnabled = true)
                }
            }
            is WebViewEvent.WebViewDisposed -> {
                state = state.copy(webViewSettings = null)
            }
            is WebViewEvent.UrlFocused -> {
                state = state.copy(shouldSelectEntireUrl = true)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Timber.d("WebViewViewModel onStop")

        updateNovel()
    }

    private fun updateNovel() {
        val currentUrl = state.webViewState.content.getCurrentUrl()
        currentUrl?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateNovel(
                    novel.copy(
                        url = it,
                        createdAt = novel.createdAt,
                        lastModified = TimeUtil.currentTimeSeconds()
                    )
                )
            }
        }
    }

    init {
        state = state.copy(
            webViewState = WebViewState(WebContent.Url(novel.url))
        )
    }
}