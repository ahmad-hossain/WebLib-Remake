package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.WebViewTopAppBar
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

data class WebViewScreenNavArgs(
    val novel: Novel
)

@Destination(navArgsDelegate = WebViewScreenNavArgs::class)
@Composable
fun WebViewScreen(
    navigator: DestinationsNavigator,
    viewModel: WebViewViewModel = hiltViewModel()
) {
    val state by viewModel.webViewScreenState
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            WebViewTopAppBar(
                url = state.addressBarText,
                onUrlEntered = { viewModel.onEvent(WebViewEvent.EnteredUrl(it)) },
                onUrlSubmitted = { viewModel.onEvent(WebViewEvent.SubmittedUrl) },
                onBackButtonClicked = { navigator.popBackStack() },
                onRefreshClicked = { viewModel.onEvent(WebViewEvent.ReloadClicked) },
                onMoreOptionsClicked = { viewModel.onEvent(WebViewEvent.MoreOptionsClicked) },
            )
        }
    ) { innerPadding ->
        Column {
            val loadingState = state.webViewState.loadingState
            if (loadingState is LoadingState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = loadingState.progress
                )
            }
            WebView(
                modifier = Modifier.padding(innerPadding),
                state = state.webViewState,
                onCreated = { it.settings.javaScriptEnabled = true },
                navigator = state.webViewNavigator,
                client = remember {
                    CustomWebViewClient(
                        onNewPageVisited = { viewModel.onEvent(WebViewEvent.NewPageVisited(it)) }
                    )
                }
            )
        }
    }
}

// todo move to separate file
class CustomWebViewClient(
    private val onNewPageVisited: (String) -> Unit
) : AccompanistWebViewClient() {
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)

        url?.let { onNewPageVisited(it) }
    }
}