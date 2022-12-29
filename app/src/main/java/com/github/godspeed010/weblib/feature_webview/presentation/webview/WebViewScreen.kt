package com.github.godspeed010.weblib.feature_webview.presentation.webview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.BasicColumn
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.WebViewTopAppBar
import com.github.godspeed010.weblib.feature_webview.util.CustomWebViewClient
import com.github.godspeed010.weblib.feature_webview.util.LoadingState
import com.github.godspeed010.weblib.feature_webview.util.WebView
import com.github.godspeed010.weblib.observeLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt

data class WebViewScreenNavArgs(
    val novel: Novel
)

@Destination(navArgsDelegate = WebViewScreenNavArgs::class)
@Composable
fun WebViewScreen(
    navigator: DestinationsNavigator,
    viewModel: WebViewViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val localDensity = LocalDensity.current
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)

    BasicColumn {
        WebViewTopAppBar(
            modifier = Modifier.offset { IntOffset(0, state.toolbarOffsetHeightPx.roundToInt()) },
            url = state.addressBarText,
            onUrlEntered = { viewModel.onEvent(WebViewEvent.EnteredUrl(it)) },
            onUrlSubmitted = { viewModel.onEvent(WebViewEvent.SubmittedUrl) },
            onBackButtonClicked = { navigator.popBackStack() },
            onRefreshClicked = { viewModel.onEvent(WebViewEvent.ReloadClicked) },
            onMoreOptionsClicked = { viewModel.onEvent(WebViewEvent.MoreOptionsToggled) },
            isMoreOptionsDropdownEnabled = state.isMoreOptionsDropdownEnabled,
            onDropdownDismissRequest = { viewModel.onEvent(WebViewEvent.MoreOptionsToggled) },
            onDarkModeOptionClicked = { viewModel.onEvent(WebViewEvent.ToggleDarkMode) },
        )

        val loadingState = state.webViewState.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = loadingState.progress
            )
        }

        WebView(
            modifier = Modifier
                .offset { IntOffset(0, state.toolbarOffsetHeightPx.roundToInt()) },
            state = state.webViewState,
            onCreated = { it.settings.javaScriptEnabled = true },
            navigator = state.webViewNavigator,
            client = remember {
                CustomWebViewClient(
                    onNewPageVisited = { viewModel.onEvent(WebViewEvent.NewPageVisited(it)) }
                )
            },
            onWebPageScroll = { x, y, oldX, oldY ->
                viewModel.onEvent(WebViewEvent.WebPageScrolled(localDensity, x, y, oldX, oldY))
            }
        )
    }
}