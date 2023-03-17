package com.github.godspeed010.weblib.feature_webview.presentation.webview

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.compose.BackHandler
import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
import com.github.godspeed010.weblib.feature_webview.util.*
import com.github.godspeed010.weblib.observeLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.*

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

    BackHandler(state.webViewNavigator.canGoBack) {
        viewModel.onEvent(WebViewEvent.BackButtonPressed)
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.onEvent(WebViewEvent.WebViewDisposed) }
    }

    val colorScheme = MaterialTheme.colorScheme
    val isSystemInDarkTheme = isSystemInDarkTheme()
    Box {
        LoadingDialog(
            isVisible = state.isLoadingDialogVisible,
            bodyText = stringResource(id = R.string.loading_saved_scroll_progress)
        )
        val client = remember {
            CustomWebViewClient(
                onNewPageVisited = { viewModel.onEvent(WebViewEvent.NewPageVisited(it)) }
            )
        }
        val chromeClient = remember { AccompanistWebChromeClient() }
        val scope = rememberCoroutineScope()
        AndroidViewBinding(
            factory = { layoutInflater, parent, attachToParent ->
                val binding = LayoutWebViewBinding.inflate(layoutInflater, parent, attachToParent)
                binding.setupListeners(navigator, viewModel)

                binding.webview.webViewClient = client
                binding.webview.webChromeClient = chromeClient

                viewModel.onEvent(WebViewEvent.WebViewCreated(binding.webview, isSystemInDarkTheme))

                scope.launch {
                    launch {
                        with(state.webViewNavigator) { binding.webview.handleNavigationEvents() }
                    }
                    launch {
                        viewModel.addressBarText.collect {
                            binding.etAddressBar.setText(it)
                        }
                    }
                }

                binding
            },
            update = {
                updateUiModeMenuItem(state.isWvDarkModeEnabled)
                updateRefreshButton(state.webViewState.isLoading)
                applyMaterialTheme(colorScheme)
                client.state = state.webViewState
                client.navigator = state.webViewNavigator
                chromeClient.state = state.webViewState
                loadUrlIfChanged(state.webViewState.content)
                updateProgressIndicator(state.webViewState.loadingState)
            }
        )
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

    // Progress Indicator
    progressIndicator.setIndicatorColor(colorScheme.primary.toArgb())
    progressIndicator.trackColor = colorScheme.surfaceVariant.toArgb()
}

private fun LayoutWebViewBinding.loadUrlIfChanged(content: WebContent) {
    when (content) {
        is WebContent.Url -> {
            val url = content.url

            if (url.isNotEmpty() && url != webview.url) {
                webview.loadUrl(url, content.additionalHttpHeaders.toMutableMap())
            }
        }
        else -> {}
    }
}

private fun LayoutWebViewBinding.updateProgressIndicator(loadingState: LoadingState) {
    if (loadingState is LoadingState.Loading) {
        progressIndicator.visibility = View.VISIBLE
        progressIndicator.setProgressCompat((loadingState.progress * 100).toInt(), true)
    } else {
        progressIndicator.visibility = View.GONE
    }
}

fun LayoutWebViewBinding.updateRefreshButton(loading: Boolean) {
    val (newIcon, newTitle) =
        if (loading) Pair(R.drawable.ic_close, R.string.cancel)
        else Pair(R.drawable.ic_refresh, R.string.reload)
    webviewToolbar.menu.findItem(R.id.reload_or_cancel).apply {
        title = root.context.getString(newTitle)
        icon = ContextCompat.getDrawable(root.context, newIcon)
    }
}

private fun LayoutWebViewBinding.updateUiModeMenuItem(isWvDarkModeEnabled: Boolean) {
    val subMenu = webviewToolbar.menu.findItem(R.id.more_options).subMenu
    val uiModeMenuItem = subMenu?.findItem(R.id.ui_mode)
    val ctx = root.context
    val (newTitle, newIcon) = when (isWvDarkModeEnabled) {
        true -> Pair(R.string.light_mode, R.drawable.ic_sun)
        false -> Pair(R.string.dark_mode,R.drawable.ic_brightness_3)
    }
    uiModeMenuItem?.apply {
        title = ctx.getString(newTitle)
        icon = ContextCompat.getDrawable(ctx, newIcon)
    }
}

private fun LayoutWebViewBinding.setupListeners(
    navigator: DestinationsNavigator,
    viewModel: WebViewViewModel
) {
    webviewToolbar.setNavigationOnClickListener { navigator.popBackStack() }
    // Nav Button onLongClick
    webviewToolbar.getChildAt(0).setOnLongClickListener {
        viewModel.onEvent(WebViewEvent.BackButtonLongPressed)
        val popupMenu = PopupMenu(webviewToolbar.context, it)
        viewModel.state.historyItems.forEachIndexed { index, historyItem ->
            popupMenu.menu.add(Menu.NONE, index, Menu.NONE, historyItem.title)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val historyItemIndex = menuItem.itemId
            viewModel.onEvent(WebViewEvent.HistoryItemClicked(historyItemIndex))
            true
        }
        popupMenu.show()
        true
    }

    etAddressBar.setOnEditorActionListener { v, actionId, _ ->
        if (actionId != EditorInfo.IME_ACTION_DONE) return@setOnEditorActionListener false
        viewModel.onEvent(WebViewEvent.SubmittedUrl(v.text.toString()))
        true
    }

    val menu = webviewToolbar.menu
    val reloadOrCancel = menu.findItem(R.id.reload_or_cancel)
    val moreOptions = menu.findItem(R.id.more_options)
    val uiMode = moreOptions.subMenu?.findItem(R.id.ui_mode)

    reloadOrCancel.setOnMenuItemClickListener {
        viewModel.onEvent(WebViewEvent.ReloadOrCancelClicked)
        true
    }
    uiMode?.setOnMenuItemClickListener {
        viewModel.onEvent(WebViewEvent.ToggleDarkMode)
        true
    }
}