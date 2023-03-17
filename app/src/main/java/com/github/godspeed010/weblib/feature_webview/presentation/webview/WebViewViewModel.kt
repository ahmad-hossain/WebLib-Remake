package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.*
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.github.godspeed010.weblib.feature_library.common.use_case.ValidatedUrl
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_webview.domain.use_case.WebViewUseCases
import com.github.godspeed010.weblib.feature_webview.util.*
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val webViewUseCases: WebViewUseCases,
    private val validatedUrlUseCase: ValidatedUrl,
    savedStateHandle: SavedStateHandle
) : ViewModel(), DefaultLifecycleObserver {

    @OptIn(ExperimentalComposeUiApi::class)
    val novel: Novel = savedStateHandle.navArgs<WebViewScreenNavArgs>().novel

    var state by mutableStateOf(WebViewScreenState(webViewNavigator = WebViewNavigator(viewModelScope)))
        private set

    private val _addressBarText = MutableSharedFlow<String>()
    val addressBarText = _addressBarText.asSharedFlow()

    @SuppressLint("SetJavaScriptEnabled")
    fun onEvent(event: WebViewEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when(event) {
            is WebViewEvent.SubmittedUrl -> {
                val validatedUrl = validatedUrlUseCase(event.url)
                Timber.d("Loading url: $validatedUrl")
                state = state.copy(webViewState = WebViewState(WebContent.Url(validatedUrl)))
            }
            is WebViewEvent.ToggleDarkMode -> {
                val isForceDarkSupported = WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)
                if (!isForceDarkSupported) return

                val forceDarkSetting = if (state.isWvDarkModeEnabled) WebSettingsCompat.FORCE_DARK_OFF else WebSettingsCompat.FORCE_DARK_ON
                WebSettingsCompat.setForceDark(state.webView?.settings ?: return, forceDarkSetting)
                state = state.copy(isWvDarkModeEnabled = !state.isWvDarkModeEnabled)
            }
            is WebViewEvent.ReloadOrCancelClicked -> {
                if (state.webViewState.isLoading)
                    state.webViewNavigator.stopLoading()
                else
                    state.webViewNavigator.reload()
            }
            is WebViewEvent.NewPageVisited -> {
                viewModelScope.launch {
                    _addressBarText.emit(event.url)
                }
            }
            is WebViewEvent.WebViewCreated -> {
                fun restoreLastScrollProgression() {
                    if (novel.scrollProgression == 0f || state.webViewState.currentPageHasError) return

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
                state = state.copy(webViewState = WebViewState(WebContent.Url(novel.url)))

                restoreLastScrollProgression()
                setupWebViewUiMode()
            }
            is WebViewEvent.WebViewDisposed -> {
                state = state.copy(webView = null)
            }
            is WebViewEvent.BackButtonLongPressed -> {
                val history = state.webView?.copyBackForwardList() ?: return
                val historyItems = (0 until history.size - 1)
                    .reversed()
                    .map { history.getItemAtIndex(it) }
                state = state.copy(historyItems = historyItems)
            }
            is WebViewEvent.HistoryItemClicked -> {
                val url = state.historyItems[event.listIndex].url
                state = state.copy(webViewState = WebViewState(WebContent.Url(url)))
            }
            is WebViewEvent.BackButtonPressed -> {
                state.webView?.goBack()
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Timber.d("WebViewViewModel onStop")

        viewModelScope.launch { webViewUseCases.updateNovel(state, novel) }
    }
}