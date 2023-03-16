package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.databinding.LayoutWebViewBinding
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.observeLifecycle
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
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)
    val state = viewModel.state

    val color = MaterialTheme.colorScheme.onSurface
    AndroidViewBinding(LayoutWebViewBinding::inflate) {
        this.webviewToolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(color.toArgb(), PorterDuff.Mode.SRC_IN)
        webview.settings.javaScriptEnabled = true
        webview.loadUrl("https://www.google.com")
    }

}