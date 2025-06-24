package com.github.godspeed010.weblib.feature_webview.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.WebViewTopAppBar
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun WebViewTopAppBar() {
    WebLibTheme {
        WebViewTopAppBar(
            url = TextFieldValue(text = "https://github.com/foo-bar/bazwhat/"),
            onUrlEntered = {},
            onUrlSubmitted = {},
            onBackButtonClicked = {},
            onBackButtonLongClicked = {},
            isWebViewLoading = false,
            onRefreshClicked = {},
            onStopLoadingClicked = {},
            onMoreOptionsClicked = {},
            isMoreOptionsDropdownEnabled = false,
            onMoreOptionsDropdownDismissRequest = {},
            onDarkModeOptionClicked = {},
            isDarkModeEnabled = true,
            onUrlFocused = {},
            historyItems = emptyList(),
            isHistoryDropdownExpanded = false,
            onHistoryDropdownDismissRequest = {},
            onClickHistoryItem = {},
        )
    }
}