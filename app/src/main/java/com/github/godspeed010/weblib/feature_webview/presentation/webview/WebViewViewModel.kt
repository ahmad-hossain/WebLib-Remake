package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.annotation.SuppressLint
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
import com.github.godspeed010.weblib.feature_webview.domain.use_case.WebViewUseCases
import com.github.godspeed010.weblib.feature_webview.util.*
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private val AppBarHeight = 56.dp

@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val webViewUseCases: WebViewUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel(), DefaultLifecycleObserver {

    @OptIn(ExperimentalComposeUiApi::class)
    val novel: Novel = savedStateHandle.navArgs<WebViewScreenNavArgs>().novel

    var state by mutableStateOf(WebViewScreenState(webViewNavigator = WebViewNavigator(viewModelScope)))
        private set

    @SuppressLint("SetJavaScriptEnabled")
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
                val isForceDarkSupported = WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)
                if (!isForceDarkSupported) return

                val forceDarkSetting = if (state.isWvDarkModeEnabled) WebSettingsCompat.FORCE_DARK_OFF else WebSettingsCompat.FORCE_DARK_ON
                WebSettingsCompat.setForceDark(state.webView?.settings ?: return, forceDarkSetting)
                state = state.copy(isWvDarkModeEnabled = !state.isWvDarkModeEnabled)
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
                fun restoreLastScrollProgression() {
                    if (novel.scrollProgression == 0f || state.webViewState.errorsForCurrentRequest.isNotEmpty()) return

                    state = state.copy(isLoadingDialogVisible = true)

                    viewModelScope.launch(Dispatchers.Main) {
                        delay(2 * 1000)
                        val scrollY = state.webView?.calculateScrollYFromProgression(novel.scrollProgression)
                        if (scrollY != null) {
                            state.webView?.scrollTo(0, scrollY)
                        }

                        state = state.copy(isLoadingDialogVisible = false)
                    }
                }

                @SuppressLint("RequiresFeature")
                fun setupWebViewUiMode() {
                    val isForceDarkSupported = WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)
                    val shouldForceDarkMode = event.isDeviceDarkModeEnabled || state.isWvDarkModeEnabled
                    if (!(isForceDarkSupported && shouldForceDarkMode)) return

                    WebSettingsCompat.setForceDark(
                        state.webView?.settings ?: return,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                    state = state.copy(isWvDarkModeEnabled = true)
                }

                state = state.copy(webView = event.webView)
                state.webView?.settings?.javaScriptEnabled = true

                restoreLastScrollProgression()
                setupWebViewUiMode()
            }
            is WebViewEvent.WebViewDisposed -> {
                state = state.copy(webView = null)
            }
            is WebViewEvent.UrlFocused -> {
                state = state.copy(shouldSelectEntireUrl = true)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Timber.d("WebViewViewModel onStop")

        viewModelScope.launch { webViewUseCases.updateNovel(state, novel) }
    }

    init {
        state = state.copy(
            webViewState = WebViewState(WebContent.Url(novel.url))
        )
    }
}