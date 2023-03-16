package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.widget.EditText
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.databinding.LayoutWebViewBinding
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.LoadingDialog
import com.github.godspeed010.weblib.feature_webview.util.CustomWebViewClient
import com.github.godspeed010.weblib.feature_webview.util.setCursorDrawableColorFilter
import com.github.godspeed010.weblib.observeLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


data class WebViewScreenNavArgs(
    val novel: Novel
)

@SuppressLint("SetJavaScriptEnabled")
@Destination(navArgsDelegate = WebViewScreenNavArgs::class)
@Composable
fun WebViewScreen(
    navigator: DestinationsNavigator,
    viewModel: WebViewViewModel = hiltViewModel()
) {
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)
    val state = viewModel.state

    val colorScheme = MaterialTheme.colorScheme
    Box {
        LoadingDialog(
            isVisible = state.isLoadingDialogVisible,
            bodyText = stringResource(id = R.string.loading_saved_scroll_progress)
        )
        AndroidViewBinding(LayoutWebViewBinding::inflate) {
            applyMaterialTheme(colorScheme)
            webview.settings.javaScriptEnabled = true
            webview.webViewClient = CustomWebViewClient(onNewPageVisited = { viewModel.onEvent(WebViewEvent.NewPageVisited(it)) })
        }
    }
}

private fun LayoutWebViewBinding.applyMaterialTheme(colorScheme: ColorScheme) {
    // Navigation Icon
    webviewToolbar.navigationIcon?.colorFilter =
        PorterDuffColorFilter(colorScheme.onSurface.toArgb(), PorterDuff.Mode.SRC_IN)

    // Trailing Icons
    val menu = webviewToolbar.menu
    val onSurfaceVariantColorFilter =
        PorterDuffColorFilter(colorScheme.onSurfaceVariant.toArgb(), PorterDuff.Mode.SRC_IN)
    for (i in 0 until menu.size()) {
        menu.getItem(i).icon?.colorFilter = onSurfaceVariantColorFilter
    }

    // Toolbar SubMenu Icons
    val subMenu = menu.findItem(R.id.more_options).subMenu
    for (i in 0 until (subMenu?.size() ?: 0)) {
        subMenu?.getItem(i)?.icon?.colorFilter = onSurfaceVariantColorFilter
    }

    // Address Bar Background
    val addressBar = webviewToolbar.findViewById<EditText>(R.id.et_address_bar)
    val roundShape = ContextCompat.getDrawable(
        this@applyMaterialTheme.root.context,
        R.drawable.shape_rounded_edittext
    )
    roundShape?.colorFilter =
        PorterDuffColorFilter(colorScheme.surfaceVariant.toArgb(), PorterDuff.Mode.SRC_IN)
    addressBar.background = roundShape

    // Address Bar Hint
    addressBar.setHintTextColor(colorScheme.onSurfaceVariant.toArgb())

    // Address Bar Text
    addressBar.setTextColor(colorScheme.onSurface.toArgb())

    // Address Bar Cursor
    val primaryColorFilter = PorterDuffColorFilter(colorScheme.primary.toArgb(), PorterDuff.Mode.SRC_IN)
    @SuppressLint("NewApi") // Api Check Handled
    when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        true -> addressBar.textCursorDrawable?.colorFilter = primaryColorFilter
        false -> addressBar.setCursorDrawableColorFilter(primaryColorFilter)
    }

    // Toolbar Container
    webviewToolbar.setBackgroundColor(colorScheme.surface.toArgb())
}