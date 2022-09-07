package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.WebViewTopAppBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@SuppressLint("UnusedMaterialScaffoldPaddingParameter") //todo remove
@Destination
@Composable
fun WebViewScreen(
    navigator: DestinationsNavigator,
    novel: Novel,
    viewModel: WebViewViewModel = hiltViewModel()
) {

    val state by viewModel.webViewScreenState
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            WebViewTopAppBar(
                url = state.url,
                onUrlEntered = { viewModel.onEvent(WebViewEvent.EnteredUrl(it)) },
                onBackButtonClicked = { navigator.popBackStack() }
            )
        }
    ) {

    }
}