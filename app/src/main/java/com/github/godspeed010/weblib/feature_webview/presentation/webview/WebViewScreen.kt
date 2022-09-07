package com.github.godspeed010.weblib.feature_webview.presentation.webview

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.WebViewTopAppBar
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
                url = state.addressBarUrl,
                onUrlEntered = { viewModel.onEvent(WebViewEvent.EnteredUrl(it)) },
                onBackButtonClicked = { navigator.popBackStack() },
                onRefreshClicked = { viewModel.onEvent(WebViewEvent.ReloadClicked) },
                onMoreOptionsClicked = { viewModel.onEvent(WebViewEvent.MoreOptionsClicked) },
            )
        }
    ) { innerPadding ->
        WebView( //todo add client
            modifier = Modifier.padding(innerPadding),
            state = state.webViewState,
            onCreated = { it.settings.javaScriptEnabled = true },
            navigator = state.webViewNavigator,
        )
    }
}