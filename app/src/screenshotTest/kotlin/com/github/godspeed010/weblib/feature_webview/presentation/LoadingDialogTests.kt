package com.github.godspeed010.weblib.feature_webview.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.feature_webview.presentation.webview.components.LoadingDialog
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun LoadingDialog() {
    WebLibTheme {
        LoadingDialog(isVisible = true, bodyText = "Loading Saved Scroll Progress")
    }
}
