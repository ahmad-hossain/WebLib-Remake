package com.github.godspeed010.weblib.feature_webview.presentation.webview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.BasicColumn
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.LoadingDialog
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
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)
    val state = viewModel.state
    val localDensity = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    LoadingDialog(
        isVisible = state.isLoadingDialogVisible,
        bodyText = stringResource(R.string.loading_saved_scroll_progress)
    )

    BasicColumn {
        WebViewTopAppBar(
            modifier = Modifier.offset { IntOffset(0, state.toolbarOffsetHeightPx.roundToInt()) },
            url = state.addressBarText,
            onUrlEntered = { viewModel.onEvent(WebViewEvent.EnteredUrl(it)) },
            onUrlSubmitted = { viewModel.onEvent(WebViewEvent.SubmittedUrl) },
            onBackButtonClicked = { navigator.popBackStack() },
            onBackButtonLongClicked = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onEvent(WebViewEvent.BackButtonLongPressed)
            },
            isWebViewLoading = state.webViewState.isLoading,
            onRefreshClicked = { viewModel.onEvent(WebViewEvent.ReloadClicked) },
            onStopLoadingClicked = { viewModel.onEvent(WebViewEvent.StopLoadingClicked) },
            onMoreOptionsClicked = { viewModel.onEvent(WebViewEvent.MoreOptionsToggled) },
            isMoreOptionsDropdownEnabled = state.isMoreOptionsDropdownEnabled,
            onMoreOptionsDropdownDismissRequest = { viewModel.onEvent(WebViewEvent.MoreOptionsToggled) },
            onDarkModeOptionClicked = { viewModel.onEvent(WebViewEvent.ToggleDarkMode) },
            isDarkModeEnabled = state.isWvDarkModeEnabled,
            onUrlFocused = { viewModel.onEvent(WebViewEvent.UrlFocused) },
            historyItems = state.historyItems,
            isHistoryDropdownExpanded = state.isHistoryDropdownExpanded,
            onHistoryDropdownDismissRequest = { viewModel.onEvent(WebViewEvent.HistoryDropdownDismissRequest) },
            onClickHistoryItem = { viewModel.onEvent(WebViewEvent.HistoryItemClicked(it)) }
        )

        val loadingState = state.webViewState.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = loadingState.progress
            )
        }

        val nestedScrollConnection = remember {
            object: NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    viewModel.onEvent(WebViewEvent.WebPageScrolled(localDensity, available.y))
                    return Offset.Zero
                }
            }
        }
        val isSystemInDarkTheme = isSystemInDarkTheme()
        WebView(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .offset { IntOffset(0, state.toolbarOffsetHeightPx.roundToInt()) },
            state = state.webViewState,
            onCreated = { viewModel.onEvent(WebViewEvent.WebViewCreated(it, isSystemInDarkTheme)) },
            onDispose = { viewModel.onEvent(WebViewEvent.WebViewDisposed) },
            navigator = state.webViewNavigator,
            client = remember {
                CustomWebViewClient(
                    onNewPageVisited = { viewModel.onEvent(WebViewEvent.NewPageVisited(it)) }
                )
            },
        )
    }
}